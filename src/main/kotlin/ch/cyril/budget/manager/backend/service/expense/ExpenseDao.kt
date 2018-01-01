package ch.cyril.budget.manager.backend.service.expense

import ch.cyril.budget.manager.backend.model.Expense
import ch.cyril.budget.manager.backend.service.Pagination
import ch.cyril.budget.manager.backend.service.SubList

interface ExpenseDao {

    fun getExpenses(
            query: ExpenseQuery?,
            sort: ExpenseSort?,
            pagination: Pagination?): SubList<Expense>

    fun getOneExpense(
            query: ExpenseQuery?,
            sort: ExpenseSort?): Expense? {
        val expenses = getExpenses(query, sort, null)
        return expenses.values.firstOrNull()
    }

    fun addExpense(expense: Expense)

    fun updateExpense(expense: Expense)

    fun deleteExpense(expense: Expense)
}