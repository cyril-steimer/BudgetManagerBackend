package ch.cyril.budget.manager.backend.service.filebased.expense

import ch.cyril.budget.manager.backend.model.*
import ch.cyril.budget.manager.backend.service.Pagination
import ch.cyril.budget.manager.backend.service.expense.*
import ch.cyril.budget.manager.backend.service.filebased.FileContentCache
import ch.cyril.budget.manager.backend.util.SubList
import java.math.BigDecimal
import java.nio.file.Path

class FilebasedExpenseDao(file: Path): ExpenseDao {

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
            return Comparator.comparing<Expense, String> { e -> e.id.id }
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

    private val contentCache: FileContentCache<Expense, Id>

    init {
        contentCache = FileContentCache(file, ExpenseParser()) { e -> e.id }
    }

    override fun getExpenses(
            query: ExpenseQuery?,
            sort: ExpenseSort?,
            pagination: Pagination?): SubList<Expense> {

        var expenses = getAllExpenses()
        if (query != null) {
            expenses = expenses.filter { query.accept(visitor, it) }
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

    override fun addExpense(expense: ExpenseWithoutId) {
        contentCache.add(expense.withId(getNewId()))
    }

    override fun updateExpense(expense: Expense) {
        contentCache.update(expense)
    }

    override fun deleteExpense(id: Id) {
        contentCache.delete(id)
    }

    override fun getPaymentMethods(): Set<PaymentMethod> {
        return getAllExpenses()
                .map { e -> e.method }
                .filter { m -> m.name.isNotEmpty() }
                .toSet()
    }

    override fun getTags(): Set<Tag> {
        return getAllExpenses()
                .flatMap { e -> e.tags }
                .toSet()
    }

    override fun getAuthors(): Set<Author> {
        return getAllExpenses()
                .map { e -> e.author }
                .filter { a -> a.name.isNotEmpty() }
                .toSet()
    }

    private fun getAllExpenses(): List<Expense> {
        return contentCache.getAll()
    }

    private fun getNewId(): Id {
        val max = getAllExpenses()
                .map { e -> e.id.id }
                .map { id -> id.toIntOrNull() }
                .filterNotNull()
                .max()
        val newId = (max ?: 0) + 1
        return Id(newId.toString())
    }
}