package ch.cyril.budget.manager.backend.service.filebased.expense

import ch.cyril.budget.manager.backend.model.Expense
import ch.cyril.budget.manager.backend.service.expense.*

class FilebasedExpenseQueryVisitor : ExpenseQueryVisitor<List<Expense>, List<Expense>> {

    override fun visitNameQuery(query: NameExpenseQuery, arg: List<Expense>): List<Expense> {
        return arg
                .filter { e -> e.name.name.contains(query.name.name) }
    }

    override fun visitIdQuery(query: IdExpenseQuery, arg: List<Expense>): List<Expense> {
        return arg
                .filter { e -> e.id == query.id }
    }

    override fun visitCategoryQuery(query: CategoryExpenseQuery, arg: List<Expense>): List<Expense> {
        return arg
                .filter { e -> e.category == query.category }
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