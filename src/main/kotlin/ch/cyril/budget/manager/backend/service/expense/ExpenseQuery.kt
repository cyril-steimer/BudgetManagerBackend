package ch.cyril.budget.manager.backend.service.expense

import ch.cyril.budget.manager.backend.model.*
import ch.cyril.budget.manager.backend.service.MathComparison
import ch.cyril.budget.manager.backend.service.StringCase
import ch.cyril.budget.manager.backend.service.StringComparison

interface ExpenseQuery {

    fun <A, R> accept(visitor: ExpenseQueryVisitor<A, R>, arg: A): R
}

class NameExpenseQuery(
        val name: Name,
        val comparison: StringComparison = StringComparison.CONTAINS,
        val case: StringCase = StringCase.CASE_INSENSITIVE) : ExpenseQuery {

    override fun <A, R> accept(visitor: ExpenseQueryVisitor<A, R>, arg: A): R {
        return visitor.visitNameQuery(this, arg)
    }
}

class MethodExpenseQuery(
        val method: PaymentMethod,
        val comparison: StringComparison = StringComparison.CONTAINS,
        val case: StringCase = StringCase.CASE_INSENSITIVE) : ExpenseQuery {

    override fun <A, R> accept(visitor: ExpenseQueryVisitor<A, R>, arg: A): R {
        return visitor.visitMethodQuery(this, arg)
    }
}

class CategoryExpenseQuery(
        val category: Category,
        val comparison: StringComparison = StringComparison.CONTAINS,
        val case: StringCase = StringCase.CASE_INSENSITIVE) : ExpenseQuery {

    override fun <A, R> accept(visitor: ExpenseQueryVisitor<A, R>, arg: A): R {
        return visitor.visitCategoryQuery(this, arg)
    }
}

class BudgetIdQuery(val budgetId: Id) : ExpenseQuery {

    override fun <A, R> accept(visitor: ExpenseQueryVisitor<A, R>, arg: A): R {
        return visitor.visitBudgetIdQuery(this, arg)
    }
}

class IdExpenseQuery(val id: Id) : ExpenseQuery {

    override fun <A, R> accept(visitor: ExpenseQueryVisitor<A, R>, arg: A): R {
        return visitor.visitIdQuery(this, arg)
    }
}

class DateExpenseQuery(
        val date: Timestamp,
        val comparison: MathComparison = MathComparison.EQ) : ExpenseQuery {

    override fun <A, R> accept(visitor: ExpenseQueryVisitor<A, R>, arg: A): R {
        return visitor.visitDateQuery(this, arg)
    }
}

class AmountExpenseQuery(
        val amount: Amount,
        val comparison: MathComparison = MathComparison.EQ) : ExpenseQuery {

    override fun <A, R> accept(visitor: ExpenseQueryVisitor<A, R>, arg: A): R {
        return visitor.visitAmountQuery(this, arg)
    }
}

class TagExpenseQuery(
        val tag: Tag,
        val comparison: StringComparison = StringComparison.CONTAINS,
        val case: StringCase = StringCase.CASE_INSENSITIVE) : ExpenseQuery {

    override fun <A, R> accept(visitor: ExpenseQueryVisitor<A, R>, arg: A): R {
        return visitor.visitTagQuery(this, arg)
    }
}

class AuthorExpenseQuery(
        val author: Author,
        val comparison: StringComparison = StringComparison.CONTAINS,
        val case: StringCase = StringCase.CASE_INSENSITIVE) : ExpenseQuery {

    override fun <A, R> accept(visitor: ExpenseQueryVisitor<A, R>, arg: A): R {
        return visitor.visitAuthorQuery(this, arg)
    }
}

class OrExpenseQuery(val queries: List<ExpenseQuery>) : ExpenseQuery {

    override fun <A, R> accept(visitor: ExpenseQueryVisitor<A, R>, arg: A): R {
        return visitor.visitOrQuery(this, arg)
    }
}

class AndExpenseQuery(val queries: List<ExpenseQuery>) : ExpenseQuery {

    override fun <A, R> accept(visitor: ExpenseQueryVisitor<A, R>, arg: A): R {
        return visitor.visitAndQuery(this, arg)
    }
}