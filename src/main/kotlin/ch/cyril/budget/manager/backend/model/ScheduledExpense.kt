package ch.cyril.budget.manager.backend.model

import ch.cyril.budget.manager.backend.util.gson.NullHandlingTypeAdapter
import ch.cyril.budget.manager.backend.util.gson.Serializer
import ch.cyril.budget.manager.backend.util.gson.Validatable
import com.google.gson.JsonParseException
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
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
        val startDate: Timestamp,
        val endDate: Timestamp,
        val schedule: Schedule,
        val lastExpense: ActualExpense?): ExpenseWithoutId {

    override fun withId(id: Id): ScheduledExpense {
        return ScheduledExpense(id, name, amount, category, method, author, tags, startDate, endDate, schedule, lastExpense)
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
        val startDate: Timestamp,
        val endDate: Timestamp,
        val schedule: Schedule,
        val lastExpense: ActualExpense?) : Expense {

    override fun withoutId(): ScheduledExpenseWithoutId {
        return ScheduledExpenseWithoutId(name, amount, category, method, author, tags, startDate, endDate, schedule, lastExpense)
    }
}

@Serializer(ScheduleTypeAdapter::class)
interface Schedule {
    fun getNextDate(today: LocalDate): LocalDate

    fun <A, R> accept(visitor: ScheduleVisitor<A, R>, arg: A): R
}

interface ScheduleVisitor<A, R> {

    fun visitWeeklySchedule(schedule: WeeklySchedule, arg: A): R

    fun visitMonthlySchedule(schedule: MonthlySchedule, arg: A): R
}

class ScheduleTypeAdapter : NullHandlingTypeAdapter<Schedule>() {

    override fun doWrite(out: JsonWriter, value: Schedule) {
        throw JsonParseException("Adapter should never be used for writing")
    }

    override fun doRead(`in`: JsonReader): Schedule {
        `in`.beginObject()
        val name = `in`.nextName()
        val schedule = when(name) {
            "dayOfWeek" -> WeeklySchedule(DayOfWeek.valueOf(`in`.nextString()))
            "dayOfMonth" -> MonthlySchedule(`in`.nextInt())
            else -> throw JsonParseException("Unknown key '$name'")
        }
        `in`.endObject()
        return schedule
    }
}

data class WeeklySchedule(val dayOfWeek: DayOfWeek) : Schedule {

    override fun getNextDate(today: LocalDate): LocalDate {
        for (days in 0 until DayOfWeek.values().size) {
            val date = today.plusDays(days.toLong() )
            if (date.dayOfWeek == dayOfWeek) {
                return date
            }
        }
        throw AssertionError("Today: ${today}, day: ${dayOfWeek}")
    }

    override fun <A, R> accept(visitor: ScheduleVisitor<A, R>, arg: A): R {
        return visitor.visitWeeklySchedule(this, arg)
    }
}

data class MonthlySchedule(val dayOfMonth: Int) : Schedule, Validatable {

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

    override fun <A, R> accept(visitor: ScheduleVisitor<A, R>, arg: A): R {
        return visitor.visitMonthlySchedule(this, arg)
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