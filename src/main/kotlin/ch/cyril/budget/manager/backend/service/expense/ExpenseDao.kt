package ch.cyril.budget.manager.backend.service.expense

import ch.cyril.budget.manager.backend.model.Expense
import ch.cyril.budget.manager.backend.service.Pagination

interface ExpenseDao {

    fun getExpenses(
            query: ExpenseQuery?,
            sort: ExpenseSort?,
            pagination: Pagination?): List<Expense>

    fun getOneExpense(
            query: ExpenseQuery?,
            sort: ExpenseSort?): Expense? {
        val expenses = getExpenses(query, sort, null)
        return expenses.firstOrNull()
    }

    fun updateExpense(expense: Expense)

    fun deleteExpense(expense: Expense)
}