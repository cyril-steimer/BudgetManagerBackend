package ch.cyril.budget.manager.backend.service.expense

import ch.cyril.budget.manager.backend.model.*
import ch.cyril.budget.manager.backend.service.Pagination
import ch.cyril.budget.manager.backend.util.SubList

interface ExpenseDao<T : Expense> {

    fun getExpenses(
            query: ExpenseQuery?,
            sort: ExpenseSort?,
            pagination: Pagination?): SubList<T>

    fun getExpenses(): SubList<T> {
        return getExpenses(null, null, null)
    }

    fun getOneExpense(
            query: ExpenseQuery?,
            sort: ExpenseSort?): T? {
        val expenses = getExpenses(query, sort, null)
        return expenses.values.firstOrNull()
    }

    fun updateExpense(expense: T)

    fun deleteExpense(id: Id)

    fun applyBulkUpdate(query: ExpenseQuery?, update: ExpenseUpdate)
}

internal class GenericUpdateVisitor<T : Expense>(private val copyWithAuthor: (T, Author) -> T) : ExpenseUpdateVisitor<T, T> {

    override fun visitAuthorExpenseUpdate(update: AuthorExpenseUpdate, arg: T): T {
        return copyWithAuthor(arg, update.author)
    }

    fun applyBulkUpdate(dao: ExpenseDao<T>, query: ExpenseQuery?, update: ExpenseUpdate) {
        val expenses = dao.getExpenses(query, null, null).values
        for (expense in expenses) {
            val updated = update.accept(this, expense)
            dao.updateExpense(updated)
        }
    }
}

interface ActualExpenseDao : ExpenseDao<ActualExpense> {

    fun addExpense(expense: ActualExpenseWithoutId): ActualExpense

    override fun applyBulkUpdate(query: ExpenseQuery?, update: ExpenseUpdate) {
        GenericUpdateVisitor<ActualExpense> { expense, author -> expense.copy(author = author) }
                .applyBulkUpdate(this, query, update)
    }

    fun getPaymentMethods(): Set<PaymentMethod>

    fun getTags(): Set<Tag>

    fun getAuthors(): Set<Author>
}

interface ExpenseTemplateDao : ExpenseDao<ExpenseTemplate> {

    fun addExpense(expense: ExpenseTemplateWithoutId): ExpenseTemplate

    override fun applyBulkUpdate(query: ExpenseQuery?, update: ExpenseUpdate) {
        GenericUpdateVisitor<ExpenseTemplate> { expense, author -> expense.copy(author = author) }
                .applyBulkUpdate(this, query, update)
    }
}

interface ScheduledExpenseDao : ExpenseDao<ScheduledExpense> {

    fun addExpense(expense: ScheduledExpenseWithoutId): ScheduledExpense

    override fun applyBulkUpdate(query: ExpenseQuery?, update: ExpenseUpdate) {
        GenericUpdateVisitor<ScheduledExpense> { expense, author -> expense.copy(author = author) }
                .applyBulkUpdate(this, query, update)
    }
}