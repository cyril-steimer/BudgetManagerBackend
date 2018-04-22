package ch.cyril.budget.manager.backend.service.expense

import ch.cyril.budget.manager.backend.model.*
import ch.cyril.budget.manager.backend.service.MathComparison
import ch.cyril.budget.manager.backend.service.StringCase
import ch.cyril.budget.manager.backend.service.StringComparison
import java.time.LocalDate

interface ExpenseQuery {

    fun <A, R> accept(visitor: ExpenseQueryVisitor<A, R>, arg: A): R
}

class NameExpenseQuery(
        val name: Name,
        val comparison: StringComparison,
        val case: StringCase) : ExpenseQuery {

    override fun <A, R> accept(visitor: ExpenseQueryVisitor<A, R>, arg: A): R {
        return visitor.visitNameQuery(this, arg)
    }
}

class MethodExpenseQuery(
        val method: PaymentMethod,
        val comparison: StringComparison,
        val case: StringCase) : ExpenseQuery {

    override fun <A, R> accept(visitor: ExpenseQueryVisitor<A, R>, arg: A): R {
        return visitor.visitMethodQuery(this, arg)
    }
}

class CategoryExpenseQuery(
        val category: Category,
        val comparison: StringComparison,
        val case: StringCase) : ExpenseQuery {

    override fun <A, R> accept(visitor: ExpenseQueryVisitor<A, R>, arg: A): R {
        return visitor.visitCategoryQuery(this, arg)
    }
}

class IdExpenseQuery(val id: Id) : ExpenseQuery {

    override fun <A, R> accept(visitor: ExpenseQueryVisitor<A, R>, arg: A): R {
        return visitor.visitIdQuery(this, arg)
    }
}

class DateExpenseQuery(val date: Timestamp, val comparison: MathComparison) : ExpenseQuery {

    override fun <A, R> accept(visitor: ExpenseQueryVisitor<A, R>, arg: A): R {
        return visitor.visitDateQuery(this, arg)
    }
}

class AmountExpenseQuery(val amount: Amount, val comparison: MathComparison) : ExpenseQuery {

    override fun <A, R> accept(visitor: ExpenseQueryVisitor<A, R>, arg: A): R {
        return visitor.visitAmountQuery(this, arg)
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