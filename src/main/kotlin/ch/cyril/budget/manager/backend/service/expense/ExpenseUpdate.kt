package ch.cyril.budget.manager.backend.service.expense

import ch.cyril.budget.manager.backend.model.Author

interface ExpenseUpdate {

    fun <A, R> accept(visitor: ExpenseUpdateVisitor<A, R>, arg: A): R
}

class AuthorExpenseUpdate(val author: Author) : ExpenseUpdate {

    override fun <A, R> accept(visitor: ExpenseUpdateVisitor<A, R>, arg: A): R {
        return visitor.visitAuthorExpenseUpdate(this, arg)
    }
}

//TODO Support more updates