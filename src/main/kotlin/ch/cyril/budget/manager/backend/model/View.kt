package ch.cyril.budget.manager.backend.model

class BudgetListExpenseFilter(val budgets: List<Budget>)

data class Title(val title: String)

data class Order(val order: Int)

data class BudgetView(
        val id: Id,
        val order: Order,
        val title: Title,
        val filter: BudgetListExpenseFilter?,
        val period: BudgetPeriod,
        val start: Timestamp?,
        val end: Timestamp?)
