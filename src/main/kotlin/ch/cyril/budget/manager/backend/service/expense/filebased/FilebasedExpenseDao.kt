package ch.cyril.budget.manager.backend.service.expense.filebased

import ch.cyril.budget.manager.backend.model.Expense
import ch.cyril.budget.manager.backend.service.expense.ExpenseDao
import ch.cyril.budget.manager.backend.service.expense.ExpenseQuery
import ch.cyril.budget.manager.backend.service.Pagination
import ch.cyril.budget.manager.backend.service.expense.ExpenseSort
import java.nio.file.Path

class FilebasedExpenseDao(val file: Path): ExpenseDao {

    private val visitor = FilebasedExpenseQueryVisitor()

    override fun getExpenses(
            query: ExpenseQuery?,
            sort: ExpenseSort?,
            pagination: Pagination?): List<Expense> {

        var expenses = getAllExpenses()
        if (query != null) {
            expenses = query.accept(visitor, expenses)
        }
        if (sort != null) {
            val comp = sort.direction.sort(sort.field.sorter)
            expenses = expenses.sortedWith(comp)
        }
        if (pagination != null) {
            val to = minOf(expenses.size, pagination.from + pagination.count)
            expenses = expenses.subList(pagination.from, to)
        }
        return expenses
    }

    override fun updateExpense(expense: Expense) {
        val expenses = getAllExpenses().toMutableList()
        val existing = expenses.indexOfFirst { e -> e.id.equals(expense.id) }
        if (existing != -1) {
            expenses.removeAt(existing)
        }
        expenses.add(expense)
        ExpenseParser().store(file, expenses)
    }

    override fun deleteExpense(expense: Expense) {
        val expenses = getAllExpenses().toMutableList()
        val existing = expenses.indexOfFirst { e -> e.id.equals(expense.id) }
        if (existing != -1) {
            expenses.removeAt(existing)
        }
        ExpenseParser().store(file, expenses)}

    private fun getAllExpenses(): List<Expense> {
        return ExpenseParser().load(file)
    }
}