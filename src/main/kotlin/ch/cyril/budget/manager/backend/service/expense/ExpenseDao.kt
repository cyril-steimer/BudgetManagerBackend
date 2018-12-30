package ch.cyril.budget.manager.backend.service.expense

import ch.cyril.budget.manager.backend.model.*
import ch.cyril.budget.manager.backend.service.Pagination
import ch.cyril.budget.manager.backend.util.SubList

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

    fun addExpense(expense: ExpenseWithoutId)

    fun updateExpense(expense: Expense)

    fun deleteExpense(id: Id)

    fun getPaymentMethods(): Set<PaymentMethod>

    fun getTags(): Set<Tag>

    fun getAuthors(): Set<Author>
}