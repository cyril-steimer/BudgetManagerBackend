package ch.cyril.budget.manager.backend.service.filebased.expense

import ch.cyril.budget.manager.backend.model.Expense
import ch.cyril.budget.manager.backend.service.MathComparisonSwitch
import ch.cyril.budget.manager.backend.service.StringCaseSwitch
import ch.cyril.budget.manager.backend.service.StringComparisonSwitch
import ch.cyril.budget.manager.backend.service.expense.*

class FilebasedExpenseQueryVisitor : ExpenseQueryVisitor<List<Expense>, List<Expense>> {

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

    override fun visitIdQuery(query: IdExpenseQuery, arg: List<Expense>): List<Expense> {
        return arg.filter { e -> e.id == query.id }
    }

    override fun visitNameQuery(query: NameExpenseQuery, arg: List<Expense>): List<Expense> {
        val caseSwitch = CaseSensitivityConverter()
        val queryCased = query.case.switch(caseSwitch, query.name.name)
        val comparator = StringComparatorSwitch(queryCased)
        return arg.filter { e ->
            val nameCased = query.case.switch(caseSwitch, e.name.name)
            query.comparison.switch(comparator, nameCased)
        }
    }

    override fun visitCategoryQuery(query: CategoryExpenseQuery, arg: List<Expense>): List<Expense> {
        val caseSwitch = CaseSensitivityConverter()
        val queryCased = query.case.switch(caseSwitch, query.category.name)
        val comparator = StringComparatorSwitch(queryCased)
        return arg.filter { e ->
            val nameCased = query.case.switch(caseSwitch, e.category.name)
            query.comparison.switch(comparator, nameCased)
        }
    }

    override fun visitDateQuery(query: DateExpenseQuery, arg: List<Expense>): List<Expense> {
        val switch = MathComparatorSwitch(query.date.timestamp)
        return arg.filter { e -> query.comparison.switch(switch, e.date.timestamp) }
    }

    override fun visitAmountQuery(query: AmountExpenseQuery, arg: List<Expense>): List<Expense> {
        val switch = MathComparatorSwitch(query.amount.amount)
        return arg.filter { e -> query.comparison.switch(switch, e.amount.amount) }
    }

    override fun visitAndQuery(query: AndExpenseQuery, arg: List<Expense>): List<Expense> {
        val res = arg.toMutableList()
        for (q in query.queries) {
            res.retainAll(q.accept(this, arg))
        }
        return res
    }

    override fun visitOrQuery(query: OrExpenseQuery, arg: List<Expense>): List<Expense> {
        val res = mutableSetOf<Expense>()
        for (q in query.queries) {
            res.addAll(q.accept(this, arg))
        }
        return res.toList()
    }
}
