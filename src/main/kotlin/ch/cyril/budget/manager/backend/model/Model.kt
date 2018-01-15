package ch.cyril.budget.manager.backend.model

import ch.cyril.budget.manager.backend.util.Identifiable
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

data class Budget(val category: Category, val amount: Amount, val period: BudgetPeriod)

enum class BudgetPeriod(override val identifier: String) : Identifiable {
    YEARLY("yearly"),
    MONTHLY("monthly")
}

data class Expense(val id: Id, val name: Name, val amount: Amount, val category: Category, val date: Timestamp)

data class Amount(val amount: BigDecimal)

data class Id(val id: Int)

data class Name(val name: String)

data class Category(val name: String)

data class Timestamp(val timestamp: Long)