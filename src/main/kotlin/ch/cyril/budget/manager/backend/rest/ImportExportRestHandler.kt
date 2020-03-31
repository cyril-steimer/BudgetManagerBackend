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

class ImportExport(
        val budgets: List<Budget>,
        val expenses: List<ActualExpense>,
        val templates: List<ExpenseTemplate>?,
        val scheduledExpenses: List<ScheduledExpense>?)

class ImportExportRestHandler(
        private val budgetDao: BudgetDao,
        private val expenseDao: ActualExpenseDao,
        private val templateDao: ExpenseTemplateDao,
        private val scheduledExpenseDao: ScheduledExpenseDao) {

    @HttpMethod(HttpVerb.GET, "/api/v1/export")
    fun export(): RestResult {
        val res = ImportExport(
                budgetDao.getBudgets().values,
                expenseDao.getExpenses().values,
                templateDao.getExpenses().values,
                scheduledExpenseDao.getExpenses().values)
        return RestResult.json(GSON.toJson(res))
    }

    @HttpMethod(HttpVerb.POST, "/api/v1/import")
    fun import(@Body values: ImportExport) {
        for (budget in values.budgets) {
            if (budgetDao.getOneBudget(budget.category) != null) {
                budgetDao.updateBudget(budget)
            } else {
                budgetDao.addBudget(budget)
            }
        }
        //TODO How can we do an import while updating existing expenses/templates/scheduled expenses?
        val oldIdToNewExpense: MutableMap<Id, ActualExpense> = HashMap();
        for (expense in values.expenses) {
            val oldId = expense.id
            val newExpense = expenseDao.addExpense(expense.withoutId())
            oldIdToNewExpense[oldId] = newExpense
        }
        for (template in values.templates ?: emptyList()) {
            templateDao.addExpense(template.withoutId())
        }
        for (scheduledExpense in values.scheduledExpenses ?: emptyList()) {
            // Each scheduled expense refers to the last expense which it generated. We need to update this reference,
            // as we won't be using the same IDs anymore.
            val lastExpense = scheduledExpense.lastExpense?.id?.let { oldIdToNewExpense[it] }
            val scheduledExpenseToAdd = scheduledExpense.copy(lastExpense = lastExpense)
            scheduledExpenseDao.addExpense(scheduledExpenseToAdd.withoutId())
        }
    }
}