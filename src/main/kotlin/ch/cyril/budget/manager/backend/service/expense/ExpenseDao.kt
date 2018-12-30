package ch.cyril.budget.manager.backend.service.expense

import ch.cyril.budget.manager.backend.model.*
import ch.cyril.budget.manager.backend.service.Pagination
import ch.cyril.budget.manager.backend.util.SubList

interface ExpenseDao {

    class GenericUpdateVisitor : ExpenseUpdateVisitor<Expense, Expense> {

        override fun visitAuthorExpenseUpdate(update: AuthorExpenseUpdate, arg: Expense): Expense {
            return arg.copy(author = update.author)
        }
    }

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

    fun applyBulkUpdate(query: ExpenseQuery?, update: ExpenseUpdate) {
        //TODO Implement more efficiently in subclasses
        val expenses = getExpenses(query, null, null).values
        val visitor = GenericUpdateVisitor()
        for (expense in expenses) {
            val updated = update.accept(visitor, expense)
            updateExpense(updated)
        }
    }

    fun getPaymentMethods(): Set<PaymentMethod>

    fun getTags(): Set<Tag>

    fun getAuthors(): Set<Author>
}