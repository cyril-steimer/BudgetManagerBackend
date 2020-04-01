package ch.cyril.budget.manager.backend.rest

import ch.cyril.budget.manager.backend.model.*
import ch.cyril.budget.manager.backend.rest.lib.Body
import ch.cyril.budget.manager.backend.rest.lib.HttpMethod
import ch.cyril.budget.manager.backend.rest.lib.HttpVerb
import ch.cyril.budget.manager.backend.rest.lib.RestResult
import ch.cyril.budget.manager.backend.service.budget.BudgetDao
import ch.cyril.budget.manager.backend.service.expense.ActualExpenseDao
import ch.cyril.budget.manager.backend.service.expense.ExpenseTemplateDao
import ch.cyril.budget.manager.backend.service.expense.ScheduledExpenseDao
import com.google.gson.JsonElement
import com.google.gson.JsonObject

private const val V1 = "V1";
private const val V2 = "V2";

private typealias BudgetV1 = BudgetWithoutId

private data class ActualExpenseV1(
        val id: Id,
        val name: Name,
        val amount: Amount,
        val category: Category,
        val date: Timestamp,
        val method: PaymentMethod,
        val author: Author,
        val tags: Set<Tag>) {

    fun withBudget(budget: Budget?): ActualExpense {
        return ActualExpense(id, name, amount, budget, date, method, author, tags)
    }
}

private data class ExpenseTemplateV1(
        val id: Id,
        val name: Name,
        val amount: Amount,
        val category: Category,
        val method: PaymentMethod,
        val author: Author,
        val tags: Set<Tag>) {

    fun withBudget(budget: Budget?): ExpenseTemplate {
        return ExpenseTemplate(id, name, amount, budget, method, author, tags)
    }
}

private data class ScheduledExpenseV1(
        val id: Id,
        val name: Name,
        val amount: Amount,
        val category: Category,
        val method: PaymentMethod,
        val author: Author,
        val tags: Set<Tag>,
        val startDate: Timestamp,
        val endDate: Timestamp?,
        val schedule: Schedule,
        val lastExpense: ActualExpense?) {

    fun withBudget(budget: Budget?): ScheduledExpense {
        return ScheduledExpense(id, name, amount, budget, method, author, tags, startDate, endDate, schedule, lastExpense)
    }
}

private class VersionedImportExport(
        val version: String,
        val content: JsonElement)

private class ImportExportV1(
        val budgets: List<BudgetV1>,
        val expenses: List<ActualExpenseV1>,
        val templates: List<ExpenseTemplateV1>?,
        val scheduledExpenses: List<ScheduledExpenseV1>?)

private class ImportExportV2(
        val budgets: List<Budget>,
        val expenses: List<ActualExpense>,
        val templates: List<ExpenseTemplate>,
        val scheduledExpenses: List<ScheduledExpense>)

class ImportExportRestHandler(
        private val budgetDao: BudgetDao,
        private val expenseDao: ActualExpenseDao,
        private val templateDao: ExpenseTemplateDao,
        private val scheduledExpenseDao: ScheduledExpenseDao) {

    @HttpMethod(HttpVerb.GET, "/api/v1/export")
    fun export(): RestResult {
        val values = ImportExportV2(
                budgetDao.getBudgets().values,
                expenseDao.getExpenses().values,
                templateDao.getExpenses().values,
                scheduledExpenseDao.getExpenses().values)
        val res = VersionedImportExport(V2, GSON.toJsonTree(values))
        return RestResult.json(GSON.toJson(res))
    }

    @HttpMethod(HttpVerb.POST, "/api/v1/import")
    fun import(@Body values: JsonObject) {
        // Previous versions made an export without a version indication
        if (values.has("version")) {
            // TODO In V2, we should skip the 'amounts' feature of budgets to save space
            val versioned = GSON.fromJson(values, VersionedImportExport::class.java)
            when (versioned.version) {
                V1 -> importV1(GSON.fromJson(versioned.content, ImportExportV1::class.java))
                V2 -> importV2(GSON.fromJson(versioned.content, ImportExportV2::class.java))
                else -> throw IllegalStateException("Unknown serialization version " + versioned.version)
            }
        } else {
            importV1(GSON.fromJson(values, ImportExportV1::class.java))
        }
    }

    private fun importV2(values: ImportExportV2) {
        val categoryToBudget = HashMap<Category, Budget>()
        for (budget in values.budgets) {
            val existing = budgetDao.getOneBudget(budget.category)
            if (existing == null) {
                val newBudget = budgetDao.addBudget(budget.withoutId())
                categoryToBudget[newBudget.category] = newBudget
            } else {
                val newBudget = budget.copy(id = existing.id)
                budgetDao.updateBudget(newBudget)
                categoryToBudget[budget.category] = newBudget
            }
        }
        //TODO How can we do an import while updating existing expenses/templates/scheduled expenses?
        val getNewBudget: (Budget?) -> Budget? = { b -> b?.category?.let { categoryToBudget[it] } }
        val oldIdToNewExpense = HashMap<Id, ActualExpense>();
        for (expense in values.expenses) {
            val oldId = expense.id
            val newExpense = expenseDao.addExpense(expense.copy(budget = getNewBudget(expense.budget)).withoutId())
            oldIdToNewExpense[oldId] = newExpense
        }
        for (template in values.templates) {
            templateDao.addExpense(template.copy(budget = getNewBudget(template.budget)).withoutId())
        }
        for (scheduledExpense in values.scheduledExpenses) {
            // Each scheduled expense refers to the last expense which it generated. We need to update this reference,
            // as we won't be using the same IDs anymore.
            val scheduledExpenseToAdd = scheduledExpense.copy(
                    lastExpense = scheduledExpense.lastExpense?.id?.let { oldIdToNewExpense[it] },
                    budget = getNewBudget(scheduledExpense.budget))
            scheduledExpenseDao.addExpense(scheduledExpenseToAdd.withoutId())
        }
    }

    private fun importV1(values: ImportExportV1) {
        val categoryToBudget = HashMap<Category, Budget>()
        // TODO: The ID currently does not matter for import. What if it ever does?
        values.budgets.forEach { b -> categoryToBudget[b.category] = Budget(Id("1"), b.category, b.amounts) }
        importV2(ImportExportV2(
                categoryToBudget.values.toList(),
                values.expenses.map { e -> e.withBudget(categoryToBudget[e.category]) },
                values.templates?.map { e -> e.withBudget(categoryToBudget[e.category]) } ?: emptyList(),
                values.scheduledExpenses?.map { e -> e.withBudget(categoryToBudget[e.category]) } ?: emptyList()))
    }
}