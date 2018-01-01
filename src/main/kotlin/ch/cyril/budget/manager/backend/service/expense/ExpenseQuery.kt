package ch.cyril.budget.manager.backend.service.expense

import ch.cyril.budget.manager.backend.model.Category
import ch.cyril.budget.manager.backend.model.Id
import ch.cyril.budget.manager.backend.model.Name
import java.time.LocalDate

interface ExpenseQuery {

    fun <A, R> accept(visitor: ExpenseQueryVisitor<A, R>, arg: A): R
}

class NameExpenseQuery(val name: Name) : ExpenseQuery {

    override fun <A, R> accept(visitor: ExpenseQueryVisitor<A, R>, arg: A): R {
        return visitor.visitNameQuery(this, arg)
    }
}

class IdExpenseQuery(val id: Id) : ExpenseQuery {

    override fun <A, R> accept(visitor: ExpenseQueryVisitor<A, R>, arg: A): R {
        return visitor.visitIdQuery(this, arg)
    }
}

class CategoryExpenseQuery(val category: Category) : ExpenseQuery {

    override fun <A, R> accept(visitor: ExpenseQueryVisitor<A, R>, arg: A): R {
        return visitor.visitCategoryQuery(this, arg)
    }
}

class SinceExpenseQuery(val since: LocalDate) : ExpenseQuery {

    override fun <A, R> accept(visitor: ExpenseQueryVisitor<A, R>, arg: A): R {
        return visitor.visitSinceQuery(this, arg)
    }
}

class BeforeExpenseQuery(val before: LocalDate) : ExpenseQuery {

    override fun <A, R> accept(visitor: ExpenseQueryVisitor<A, R>, arg: A): R {
        return visitor.visitBeforeQuery(this, arg)
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