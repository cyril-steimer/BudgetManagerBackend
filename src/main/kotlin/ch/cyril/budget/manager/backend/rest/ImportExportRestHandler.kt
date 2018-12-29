package ch.cyril.budget.manager.backend.rest

import ch.cyril.budget.manager.backend.model.Budget
import ch.cyril.budget.manager.backend.model.Expense
import ch.cyril.budget.manager.backend.rest.lib.Body
import ch.cyril.budget.manager.backend.rest.lib.HttpMethod
import ch.cyril.budget.manager.backend.rest.lib.HttpVerb
import ch.cyril.budget.manager.backend.rest.lib.RestResult
import ch.cyril.budget.manager.backend.service.budget.BudgetDao
import ch.cyril.budget.manager.backend.service.expense.ExpenseDao
import ch.cyril.budget.manager.backend.service.expense.IdExpenseQuery

class ImportExport(val budgets: List<Budget>, val expenses: List<Expense>)

class ImportExportRestHandler(
        private val budgetDao: BudgetDao,
        private val expenseDao: ExpenseDao) {

    @HttpMethod(HttpVerb.GET, "/api/v1/export")
    fun export(): RestResult {
        val budgets = budgetDao.getBudgets().values
        val expenses = expenseDao.getExpenses(null, null, null).values
        val res = ImportExport(budgets, expenses)
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
        for (expense in values.expenses) {
            if (expenseDao.getOneExpense(IdExpenseQuery(expense.id), null) != null) {
                expenseDao.updateExpense(expense)
            } else {
                expenseDao.addExpense(expense.withoutId())
            }
        }
    }
}