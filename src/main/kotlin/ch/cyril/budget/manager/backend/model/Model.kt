package ch.cyril.budget.manager.backend.model

import java.math.BigDecimal
import java.time.LocalDate

data class Budget(val category: Category, val amount: Amount)

data class Expense(val id: Id, val name: Name, val amount: Amount, val category: Category, val date: LocalDate)

data class Amount(val amount: BigDecimal)

data class Id(val id: Int)

data class Name(val name: String)

data class Category(val name: String)