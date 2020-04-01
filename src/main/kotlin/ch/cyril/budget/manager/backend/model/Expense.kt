package ch.cyril.budget.manager.backend.model

interface Expense {
    val id: Id
    val name: Name
    val budget: Budget?
    val amount: Amount
    val method: PaymentMethod
    val author: Author
    val tags: Set<Tag>

    fun withoutId(): ExpenseWithoutId
}

interface ExpenseWithoutId {
    val name: Name
    val amount: Amount
    val budget: Budget?
    val method: PaymentMethod
    val author: Author
    val tags: Set<Tag>

    fun withId(id: Id): Expense
}

data class ActualExpenseWithoutId(
        override val name: Name,
        override val amount: Amount,
        override val budget: Budget?,
        val date: Timestamp,
        override val method: PaymentMethod,
        override val author: Author,
        override val tags: Set<Tag>) : ExpenseWithoutId {

    override fun withId(id: Id) = ActualExpense(id, name, amount, budget, date, method, author, tags)
}

data class ActualExpense(
        override val id: Id,
        override val name: Name,
        override val amount: Amount,
        override val budget: Budget?,
        val date: Timestamp,
        override val method: PaymentMethod,
        override val author: Author,
        override val tags: Set<Tag>) : Expense {

    override fun withoutId() = ActualExpenseWithoutId(name, amount, budget, date, method, author, tags)
}

data class ExpenseTemplateWithoutId(
        override val name: Name,
        override val amount: Amount,
        override val budget: Budget?,
        override val method: PaymentMethod,
        override val author: Author,
        override val tags: Set<Tag>) : ExpenseWithoutId {

    override fun withId(id: Id) = ExpenseTemplate(id, name, amount, budget, method, author, tags)
}

data class ExpenseTemplate(
        override val id: Id,
        override val name: Name,
        override val amount: Amount,
        override val budget: Budget?,
        override val method: PaymentMethod,
        override val author: Author,
        override val tags: Set<Tag>) : Expense {

    override fun withoutId() = ExpenseTemplateWithoutId(name, amount, budget, method, author, tags)
}