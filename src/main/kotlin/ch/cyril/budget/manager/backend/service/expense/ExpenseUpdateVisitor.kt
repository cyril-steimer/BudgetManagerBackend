package ch.cyril.budget.manager.backend.service.expense

interface ExpenseUpdateVisitor<A, R> {

    fun visitAuthorExpenseUpdate (update: AuthorExpenseUpdate, arg: A): R
}