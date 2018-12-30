package ch.cyril.budget.manager.backend.service.expense

interface ExpenseQueryVisitor<A, R> {

    fun visitNameQuery(query: NameExpenseQuery, arg: A): R

    fun visitMethodQuery(query: MethodExpenseQuery, arg: A): R

    fun visitIdQuery(query: IdExpenseQuery, arg: A): R

    fun visitCategoryQuery(query: CategoryExpenseQuery, arg: A): R

    fun visitDateQuery(query: DateExpenseQuery, arg: A): R

    fun visitAmountQuery(query: AmountExpenseQuery, arg: A): R

    fun visitTagQuery(query: TagExpenseQuery, arg: A): R

    fun visitAuthorQuery(query: AuthorExpenseQuery, arg: A): R

    fun visitOrQuery(query: OrExpenseQuery, arg: A): R

    fun visitAndQuery(query: AndExpenseQuery, arg: A): R
}