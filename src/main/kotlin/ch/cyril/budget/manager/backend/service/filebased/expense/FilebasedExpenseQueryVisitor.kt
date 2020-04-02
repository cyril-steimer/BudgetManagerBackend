package ch.cyril.budget.manager.backend.service.filebased.expense

import ch.cyril.budget.manager.backend.model.ActualExpense
import ch.cyril.budget.manager.backend.model.Expense
import ch.cyril.budget.manager.backend.service.*
import ch.cyril.budget.manager.backend.service.expense.*

class FilebasedExpenseQueryVisitor : ExpenseQueryVisitor<Expense, Boolean> {

    private class MathComparatorSwitch<T : Comparable<T>>(val query: T) : MathComparisonSwitch<T, Boolean> {

        override fun caseLt(arg: T): Boolean {
            return arg < query
        }

        override fun caseLte(arg: T): Boolean {
            return arg <= query
        }

        override fun caseEq(arg: T): Boolean {
            return arg == query
        }

        override fun caseNeq(arg: T): Boolean {
            return arg != query
        }

        override fun caseGte(arg: T): Boolean {
            return arg >= query
        }

        override fun caseGt(arg: T): Boolean {
            return arg > query
        }
    }

    private class CaseSensitivityConverter : StringCaseSwitch<String, String> {

        override fun caseCaseSensitive(arg: String): String {
            return arg
        }

        override fun caseCaseInsensitive(arg: String): String {
            return arg.toLowerCase()
        }
    }

    private class StringComparatorSwitch(val query: String) : StringComparisonSwitch<String, Boolean> {

        override fun caseStartsWith(arg: String): Boolean {
            return arg.startsWith(query)
        }

        override fun caseContains(arg: String): Boolean {
            return arg.contains(query)
        }

        override fun caseEndsWith(arg: String): Boolean {
            return arg.endsWith(query)
        }

        override fun caseEq(arg: String): Boolean {
            return arg == query
        }
    }

    private val caseSwitch = CaseSensitivityConverter()

    override fun visitIdQuery(query: IdExpenseQuery, arg: Expense): Boolean {
        return arg.id == query.id
    }

    override fun visitNameQuery(query: NameExpenseQuery, arg: Expense): Boolean {
        return doesStringMatch(query.name.name, query.case, query.comparison, arg.name.name)
    }

    override fun visitMethodQuery(query: MethodExpenseQuery, arg: Expense): Boolean {
        return doesStringMatch(query.method.name, query.case, query.comparison, arg.method.name)
    }

    override fun visitCategoryQuery(query: CategoryExpenseQuery, arg: Expense): Boolean {
        val category = arg.budget?.category?.name ?: return false
        return doesStringMatch(query.category.name, query.case, query.comparison, category)
    }

    override fun visitBudgetIdQuery(query: BudgetIdQuery, arg: Expense): Boolean {
        return arg.budget?.let { it.id == query.budgetId } ?: false
    }

    override fun visitAuthorQuery(query: AuthorExpenseQuery, arg: Expense): Boolean {
        return doesStringMatch(query.author.name, query.case, query.comparison, arg.author.name)
    }

    override fun visitDateQuery(query: DateExpenseQuery, arg: Expense): Boolean {
        if (arg is ActualExpense) {
            val switch = MathComparatorSwitch(query.date.getEpochDay())
            return query.comparison.switch(switch, arg.date.getEpochDay())
        }
        return false
    }

    override fun visitAmountQuery(query: AmountExpenseQuery, arg: Expense): Boolean {
        val switch = MathComparatorSwitch(query.amount.amount)
        return query.comparison.switch(switch, arg.amount.amount)
    }

    override fun visitTagQuery(query: TagExpenseQuery, arg: Expense): Boolean {
        return arg.tags.any { tag -> doesStringMatch(query.tag.name, query.case, query.comparison, tag.name) }
    }

    override fun visitAndQuery(query: AndExpenseQuery, arg: Expense): Boolean {
        return query.queries.all { it.accept(this, arg) }
    }

    override fun visitOrQuery(query: OrExpenseQuery, arg: Expense): Boolean {
        return query.queries.any { it.accept(this, arg) }
    }

    private fun doesStringMatch(
            expected: String,
            case: StringCase,
            comparison: StringComparison,
            actual: String): Boolean {

        val expectedCased = case.switch(caseSwitch, expected)
        val comparator = StringComparatorSwitch(expectedCased)
        val actualCased = case.switch(caseSwitch, actual)
        return comparison.switch(comparator, actualCased)
    }
}
