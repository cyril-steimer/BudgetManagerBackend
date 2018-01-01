package ch.cyril.budget.manager.backend.service.expense

interface ExpenseQueryVisitor<A, R> {

    fun visitNameQuery(query: NameExpenseQuery, arg: A): R

    fun visitIdQuery(query: IdExpenseQuery, arg: A): R

    fun visitCategoryQuery(query: CategoryExpenseQuery, arg: A): R

    fun visitSinceQuery(query: SinceExpenseQuery, arg: A): R

    fun visitBeforeQuery(query: BeforeExpenseQuery, arg: A): R

    fun visitOrQuery(query: OrExpenseQuery, arg: A): R

    fun visitAndQuery(query: AndExpenseQuery, arg: A): R
}