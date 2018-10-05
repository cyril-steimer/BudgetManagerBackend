package ch.cyril.budget.manager.backend.service.filebased.expense

import ch.cyril.budget.manager.backend.model.Expense
import ch.cyril.budget.manager.backend.model.Id
import ch.cyril.budget.manager.backend.model.PaymentMethod
import ch.cyril.budget.manager.backend.model.Tag
import ch.cyril.budget.manager.backend.service.Pagination
import ch.cyril.budget.manager.backend.service.expense.*
import ch.cyril.budget.manager.backend.util.SubList
import java.math.BigDecimal
import java.nio.file.Path

class FilebasedExpenseDao(val file: Path): ExpenseDao {

    private class ComparatorSortDirectionSwitch<T> : SortDirectionSwitch<Comparator<T>, Comparator<T>> {

        override fun caseAscending(arg: Comparator<T>): Comparator<T> {
            return arg
        }

        override fun caseDescending(arg: Comparator<T>): Comparator<T> {
            return arg.reversed()
        }
    }

    private class ComparatorSortFieldSwitch : ExpenseSortFieldSwitch<Unit, Comparator<Expense>> {

        override fun caseId(arg: Unit): Comparator<Expense> {
            return Comparator.comparing<Expense, Int> { e -> e.id.id }
        }

        override fun caseAmount(arg: Unit): Comparator<Expense> {
            return Comparator.comparing<Expense, BigDecimal> { e -> e.amount.amount }
        }

        override fun caseName(arg: Unit): Comparator<Expense> {
            return Comparator.comparing<Expense, String> { e -> e.name.name }
        }

        override fun caseCategory(arg: Unit): Comparator<Expense> {
            return Comparator.comparing<Expense, String> { e -> e.category.name }
        }

        override fun caseDate(arg: Unit): Comparator<Expense> {
            return Comparator.comparing<Expense, Long> { e -> e.date.timestamp }
        }
    }

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
            var comparator = sort.field.switch(ComparatorSortFieldSwitch(), Unit)
            comparator = sort.direction.switch(ComparatorSortDirectionSwitch(), comparator)
            expenses = expenses.sortedWith(comparator)
        }
        if (pagination != null) {
            return SubList.of(expenses, pagination.from, pagination.count)
        }
        return SubList.of(expenses)
    }

    override fun addExpense(expense: Expense) {
        val id = getNewId()
        val newExpense = Expense(id, expense.name, expense.amount, expense.category, expense.date, expense.method, expense.tags)
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
        ExpenseParser().store(file, expenses)
    }

    override fun getPaymentMethods(): Set<PaymentMethod> {
        return getExpenses(null, null, null)
                .values
                .map { e -> e.method }
                .toSet()
    }

    override fun getTags(): Set<Tag> {
        return getExpenses(null, null, null)
                .values
                .flatMap { e -> e.tags }
                .toSet()
    }

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