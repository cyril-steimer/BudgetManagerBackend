package ch.cyril.budget.manager.backend.service.filebased.expense

import ch.cyril.budget.manager.backend.model.Expense
import ch.cyril.budget.manager.backend.model.Id
import ch.cyril.budget.manager.backend.service.expense.ExpenseDao
import ch.cyril.budget.manager.backend.service.expense.ExpenseQuery
import ch.cyril.budget.manager.backend.service.Pagination
import ch.cyril.budget.manager.backend.util.SubList
import ch.cyril.budget.manager.backend.service.expense.ExpenseSort
import java.nio.file.Path

class FilebasedExpenseDao(val file: Path): ExpenseDao {

    private val visitor = FilebasedExpenseQueryVisitor()

    override fun getExpenses(
            query: ExpenseQuery?,
            sort: ExpenseSort?,
            pagination: Pagination?): SubList<Expense> {

        var expenses = getAllExpenses()
        if (query != null) {
            expenses = query.accept(visitor, expenses)
        }
        if (sort != null) {
            val comp = sort.direction.sort(sort.field.sorter)
            expenses = expenses.sortedWith(comp)
        }
        if (pagination != null) {
            return SubList.of(expenses, pagination.from, pagination.count)
        }
        return SubList.of(expenses)
    }

    override fun addExpense(expense: Expense) {
        val id = getNewId()
        val newExpense = Expense(id, expense.name, expense.amount, expense.category, expense.date)
        val expenses = getAllExpenses().toMutableList()
        expenses.add(newExpense)
        ExpenseParser().store(file, expenses)
    }

    override fun updateExpense(expense: Expense) {
        val expenses = getAllExpenses().toMutableList()
        val existing = expenses.indexOfFirst { e -> e.id.equals(expense.id) }
        if (existing == -1) {
            throw IllegalArgumentException("The expense with Id '${expense.id.id}' does not exist")
        }
        expenses.removeAt(existing)
        expenses.add(expense)
        ExpenseParser().store(file, expenses)
    }

    override fun deleteExpense(expense: Expense) {
        val expenses = getAllExpenses().toMutableList()
        val existing = expenses.indexOfFirst { e -> e.id.equals(expense.id) }
        if (existing == -1) {
            throw IllegalArgumentException("The expense with Id '${expense.id.id}' does not exist")
        }
        if (existing != -1) {
            expenses.removeAt(existing)
        }
        ExpenseParser().store(file, expenses)}

    private fun getAllExpenses(): List<Expense> {
        return ExpenseParser().load(file)
    }

    private fun getNewId(): Id {
        val max = getAllExpenses()
                .map { e -> e.id.id }
                .max()
        val newId = (max ?: 0) + 1
        return Id(newId)
    }
}