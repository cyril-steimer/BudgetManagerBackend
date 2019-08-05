package ch.cyril.budget.manager.backend.model

import ch.cyril.budget.manager.backend.util.gson.Validatable
import com.google.gson.JsonParseException
import java.lang.AssertionError
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month

data class ScheduledExpenseWithoutId(
        override val name: Name,
        override val amount: Amount,
        override val category: Category,
        override val method: PaymentMethod,
        override val author: Author,
        override val tags: Set<Tag>,
        val schedule: Schedule): ExpenseWithoutId {

    override fun withId(id: Id): Expense {
        return ScheduledExpense(id, name, amount, category, method, author, tags, schedule, null)
    }
}

data class ScheduledExpense(
        override val id: Id,
        override val name: Name,
        override val amount: Amount,
        override val category: Category,
        override val method: PaymentMethod,
        override val author: Author,
        override val tags: Set<Tag>,
        val schedule: Schedule,
        val lastExpense: ActualExpense?) : Expense {

    override fun withoutId(): ExpenseWithoutId {
        return ScheduledExpenseWithoutId(name, amount, category, method, author, tags, schedule)
    }
}

interface Schedule {
    fun getNextDate(today: LocalDate): LocalDate
}

class WeeklySchedule(private val dayOfWeek: DayOfWeek) : Schedule {

    override fun getNextDate(today: LocalDate): LocalDate {
        for (days in 0 until DayOfWeek.values().size) {
            val date = today.plusDays(days.toLong() )
            if (date.dayOfWeek == dayOfWeek) {
                return date
            }
        }
        throw AssertionError("Today: ${today}, day: ${dayOfWeek}")
    }
}

class MonthlySchedule(private val dayOfMonth: Int) : Schedule, Validatable {

    override fun getNextDate(today: LocalDate): LocalDate {
        if (dayOfMonth > today.lengthOfMonth()) {
            return LocalDate.of(today.year, today.month, today.lengthOfMonth())
        }
        for (days in 0 until today.lengthOfMonth()) {
            val date = today.plusDays(days.toLong())
            if (date.dayOfMonth == dayOfMonth) {
                return date
            }
        }
        throw AssertionError("Today: ${today}, day: ${dayOfMonth}")
    }

    override fun validate() {
        val maxDays = Month.values()
                .map { it.maxLength() }
                .max() ?: 0
        if (dayOfMonth < 1 || dayOfMonth > maxDays) {
            throw JsonParseException("Day of month must be between 1 and ${maxDays}, was ${dayOfMonth}")
        }
    }
}