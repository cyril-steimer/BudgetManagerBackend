package ch.cyril.budget.manager.backend.model

import ch.cyril.budget.manager.backend.util.gson.NullHandlingTypeAdapter
import ch.cyril.budget.manager.backend.util.gson.Validatable
import com.google.gson.JsonParseException
import com.google.gson.annotations.JsonAdapter
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
        val endDate: Timestamp?,
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
        val endDate: Timestamp?,
        val schedule: Schedule,
        val lastExpense: ActualExpense?) : Expense {

    override fun withoutId(): ScheduledExpenseWithoutId {
        return ScheduledExpenseWithoutId(name, amount, category, method, author, tags, startDate, endDate, schedule, lastExpense)
    }

    fun getExpenseToAdd(today: Timestamp): ActualExpenseWithoutId {
        return ActualExpenseWithoutId(name, amount, category, today, method, author, tags)
    }
}

@JsonAdapter(ScheduleTypeAdapter::class)
interface Schedule {
    fun getNextDate(from: Timestamp): Timestamp {
        val date = getNextDate(LocalDate.ofEpochDay(from.getEpochDay()))
        return Timestamp.ofEpochDay(date.toEpochDay())
    }

    fun getNextDate(from: LocalDate): LocalDate

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

    override fun getNextDate(from: LocalDate): LocalDate {
        for (days in 0 until DayOfWeek.values().size) {
            val date = from.plusDays(days.toLong() )
            if (date.dayOfWeek == dayOfWeek) {
                return date
            }
        }
        throw AssertionError("Today: ${from}, day: ${dayOfWeek}")
    }

    override fun <A, R> accept(visitor: ScheduleVisitor<A, R>, arg: A): R {
        return visitor.visitWeeklySchedule(this, arg)
    }
}

data class MonthlySchedule(val dayOfMonth: Int) : Schedule, Validatable {

    override fun getNextDate(from: LocalDate): LocalDate {
        if (dayOfMonth > from.lengthOfMonth()) {
            return LocalDate.of(from.year, from.month, from.lengthOfMonth())
        }
        for (days in 0 until from.lengthOfMonth()) {
            val date = from.plusDays(days.toLong())
            if (date.dayOfMonth == dayOfMonth) {
                return date
            }
        }
        throw AssertionError("Today: ${from}, day: ${dayOfMonth}")
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