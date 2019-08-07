package ch.cyril.budget.manager.backend.service.filebased.expense

import ch.cyril.budget.manager.backend.model.*
import ch.cyril.budget.manager.backend.service.Pagination
import ch.cyril.budget.manager.backend.service.expense.*
import ch.cyril.budget.manager.backend.service.filebased.FileContentCache
import ch.cyril.budget.manager.backend.service.filebased.JsonBasedFileParser
import ch.cyril.budget.manager.backend.util.SubList
import java.math.BigDecimal
import java.nio.file.Path

abstract class FilebasedExpenseDao<T : Expense>(file: Path, parser: JsonBasedFileParser<T>): ExpenseDao<T> {

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
            return Comparator.comparing<Expense, Long> { if (it is ActualExpense) it.date.timestamp else 0 }
        }
    }

    protected val contentCache: FileContentCache<T, Id>

    private val visitor = FilebasedExpenseQueryVisitor()

    init {
        contentCache = FileContentCache(file, parser) { e -> e.id }
    }

    override fun getExpenses(
            query: ExpenseQuery?,
            sort: ExpenseSort?,
            pagination: Pagination?): SubList<T> {

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

    override fun updateExpense(expense: T) {
        contentCache.update(expense)
    }

    override fun deleteExpense(id: Id) {
        contentCache.delete(id)
    }

    protected fun getAllExpenses(): List<T> {
        return contentCache.getAll()
    }

    protected fun getNewId(): Id {
        val max = getAllExpenses()
                .map { e -> e.id.id }
                .map { id -> id.toIntOrNull() }
                .filterNotNull()
                .max()
        val newId = (max ?: 0) + 1
        return Id(newId.toString())
    }
}

class FilebasedActualExpenseDao(file: Path) :
        FilebasedExpenseDao<ActualExpense>(file, ActualExpenseParser()),
        ActualExpenseDao {

    override fun addExpense(expense: ActualExpenseWithoutId) {
        contentCache.add(expense.withId(getNewId()))
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
}

class FilebasedExpenseTemplateDao(file: Path) :
        FilebasedExpenseDao<ExpenseTemplate>(file, ExpenseTemplateParser()),
        ExpenseTemplateDao {

    override fun addExpense(expense: ExpenseTemplateWithoutId) {
        contentCache.add(expense.withId(getNewId()))
    }
}

class FilebasedScheduledExpenseDao(file: Path) :
        FilebasedExpenseDao<ScheduledExpense>(file, ScheduledExpenseParser()),
        ScheduledExpenseDao {

    override fun addExpense(expense: ScheduledExpenseWithoutId) {
        contentCache.add(expense.withId(getNewId()))
    }
}
