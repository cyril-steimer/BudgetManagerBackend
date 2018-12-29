package ch.cyril.budget.manager.backend.model

import ch.cyril.budget.manager.backend.util.Identifiable
import java.math.BigDecimal

data class Budget(val category: Category, val amounts: List<BudgetAmount>)

// TODO Don't allow overlapping budget amounts in all DAOs (or maybe the validate method?)

data class BudgetAmount(
        val amount: Amount,
        val period: BudgetPeriod,
        val from: MonthYear,
        val to: MonthYear) : Validatable {

    override fun validate() {
        //TODO Validate amount, from < to
    }
}

data class MonthYear(val month: Int, val year: Int) : Validatable {

    override fun validate() {
        //TODO Validate month
    }

    fun toEpochMonth(): Int {
        return (year * 12) + (month - 1)
    }

    companion object {
        fun fromEpochMonth(epochMonth: Int): MonthYear {
            val month = epochMonth % 12
            val year = epochMonth / 12
            return MonthYear(month + 1, year)
        }
    }
}

enum class BudgetPeriod(override val identifier: String) : Identifiable {
    YEARLY("yearly"),
    MONTHLY("monthly")
}

data class MonthYearPeriod(val from: MonthYear, val to: MonthYear)

data class BudgetInPeriod(val category: Category, val amount: Amount)

data class ExpenseWithoutId(
        val name: Name,
        val amount: Amount,
        val category: Category,
        val date: Timestamp,
        val method: PaymentMethod,
        val tags: Set<Tag>): Validatable {

    override fun validate() {
        amount.validate()
    }
}

data class Expense(
        val id: Id,
        val name: Name,
        val amount: Amount,
        val category: Category,
        val date: Timestamp,
        val method: PaymentMethod,
        val tags: Set<Tag>) : Validatable {

    override fun validate() {
        amount.validate()
    }

    fun withoutId() = ExpenseWithoutId(name, amount, category, date, method, tags)
}

data class Tag(val name: String)

data class PaymentMethod(val name: String)

data class Amount(val amount: BigDecimal) : Validatable {

    override fun validate() {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw ValidationException("Amount must be greater than zero")
        }
    }
}

data class Id(val id: String)

data class Name(val name: String)

data class Category(val name: String)

data class Timestamp(val timestamp: Long)