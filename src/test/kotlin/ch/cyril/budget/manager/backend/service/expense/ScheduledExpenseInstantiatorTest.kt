package ch.cyril.budget.manager.backend.service.expense

import ch.cyril.budget.manager.backend.model.*
import ch.cyril.budget.manager.backend.util.SubList
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.*

class ScheduledExpenseInstantiatorTest {

    private val scheduledExpenseDao = mockk<ScheduledExpenseDao>(relaxed = true)

    private val actualExpenseDao = mockk<ActualExpenseDao>(relaxed = true)

    private val instantiator = ScheduledExpenseInstantiator(scheduledExpenseDao, actualExpenseDao)

    @Test
    fun addOnEndDate() {
        val today = Timestamp.parse("2019-01-10")
        val start = Timestamp.parse("2019-01-09")

        val schedule = MonthlySchedule(10)
        val scheduledExpense = scheduledExpense(start, today, schedule)
        val addedWithId = mockAddingExpense(scheduledExpense, today)

        instantiator.run(today)

        verifyAdded(scheduledExpense, addedWithId)
    }

    @Test
    fun addMissingExpensesSinceStart() {
        val today = Timestamp.parse("2019-01-20")

        val schedule = MonthlySchedule(10)
        val scheduledExpense = scheduledExpense(Timestamp.parse("2018-11-20"), null, schedule)
        val added1 = mockAddingExpense(scheduledExpense, "2018-12-10")
        val added2 = mockAddingExpense(scheduledExpense, "2019-01-10")

        instantiator.run(today)

        verifyAdded(scheduledExpense, added1, added2)
    }

    @Test
    fun addMissingExpensesForInactiveExpense() {
        val today = Timestamp.parse("2018-10-30")

        val schedule = MonthlySchedule(15)
        val scheduledExpense = scheduledExpense(Timestamp.parse("2018-04-20"), Timestamp.parse("2018-07-10"), schedule)
        val added1 = mockAddingExpense(scheduledExpense, "2018-05-15")
        val added2 = mockAddingExpense(scheduledExpense, "2018-06-15")

        instantiator.run(today)

        verifyAdded(scheduledExpense, added1, added2)
    }

    @Test
    fun addMissingExpensesSinceLastExpense() {
        val today = Timestamp.parse("2019-01-20")
        val lastExpenseTimestamp = Timestamp.parse("2018-10-10")

        val schedule = MonthlySchedule(10)
        val scheduledExpense = scheduledExpense(Timestamp.ofEpochDay(0), null, schedule, lastExpenseTimestamp)
        val added1 = mockAddingExpense(scheduledExpense, "2018-11-10")
        val added2 = mockAddingExpense(scheduledExpense, "2018-12-10")
        val added3 = mockAddingExpense(scheduledExpense, "2019-01-10")

        instantiator.run(today)

        verifyAdded(scheduledExpense, added1, added2, added3)
    }

    @Test
    fun dontAddTwiceOnSameDate() {
        val today = Timestamp.parse("2018-01-02")

        val schedule = MonthlySchedule(2)
        scheduledExpense(Timestamp.ofEpochDay(0), null, schedule, today)

        instantiator.run(today)

        verify(exactly = 0) { actualExpenseDao.addExpense(any()) }
    }

    @Test
    fun addOnStartDate() {
        val today = Timestamp.parse("2019-01-20")

        val schedule = MonthlySchedule(20)
        val scheduledExpense = scheduledExpense(today, null, schedule)
        val addedWithId = mockAddingExpense(scheduledExpense, today)

        instantiator.run(today)

        verifyAdded(scheduledExpense, addedWithId)
    }

    private fun mockAddingExpense(scheduledExpense: ScheduledExpense, date: Timestamp): ActualExpense {
        val withoutId = scheduledExpense.getExpenseToAdd(date)
        val withId = withoutId.withId(Id(UUID.randomUUID().toString()))
        every { actualExpenseDao.addExpense(withoutId) } returns withId
        return withId
    }

    private fun mockAddingExpense(scheduledExpense: ScheduledExpense, date: String): ActualExpense {
        return mockAddingExpense(scheduledExpense, Timestamp.parse(date))
    }

    private fun verifyAdded(scheduledExpense: ScheduledExpense, vararg expenses: ActualExpense) {
        verifyOrder {
            for (expense in expenses) {
                actualExpenseDao.addExpense(expense.withoutId())
                scheduledExpenseDao.updateExpense(scheduledExpense.copy(lastExpense = expense))
            }
        }
    }

    private fun scheduledExpense(startDate: Timestamp, endDate: Timestamp?, schedule: Schedule): ScheduledExpense {
        return scheduledExpense(startDate, endDate, schedule, null)
    }

    private fun scheduledExpense(startDate: Timestamp, endDate: Timestamp?, schedule: Schedule, lastExpenseTimestamp: Timestamp?): ScheduledExpense {
        val id = Id("id")
        val name = Name("name")
        val amount = Amount(BigDecimal(100))
        val category = Category("category")
        val method = PaymentMethod("method")
        val author = Author("author")
        val tags = emptySet<Tag>()
        var lastExpense: ActualExpense? = null
        if (lastExpenseTimestamp != null) {
            lastExpense = ActualExpense(id, name, amount, category, lastExpenseTimestamp, method, author, tags)
        }
        val res = ScheduledExpense(id, name, amount, category, method, author, tags, startDate, endDate, schedule, lastExpense)
        every { scheduledExpenseDao.getExpenses() } returns SubList.of(listOf(res))
        return res
    }
}