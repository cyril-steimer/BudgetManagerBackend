package ch.cyril.budget.manager.backend.model

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month

class ScheduleTest {

    @Test
    fun weekly() {
        val schedule = WeeklySchedule(DayOfWeek.WEDNESDAY)
        var today = LocalDate.of(2019, Month.AUGUST, 2)

        Assertions.assertEquals(DayOfWeek.FRIDAY, today.dayOfWeek)
        Assertions.assertEquals(LocalDate.of(2019, Month.AUGUST, 7), schedule.getNextDate(today))

        today = LocalDate.of(2019, Month.AUGUST, 7)

        Assertions.assertEquals(DayOfWeek.WEDNESDAY, today.dayOfWeek)
        Assertions.assertEquals(today, schedule.getNextDate(today))

        today = LocalDate.of(2019, Month.DECEMBER, 30)

        Assertions.assertEquals(DayOfWeek.MONDAY, today.dayOfWeek)
        Assertions.assertEquals(LocalDate.of(2020, Month.JANUARY, 1), schedule.getNextDate(today))
    }

    @Test
    fun monthlyNormalDay() {
        val schedule = MonthlySchedule(15)
        var today = LocalDate.of(2019, Month.AUGUST, 10)

        Assertions.assertEquals(LocalDate.of(2019, Month.AUGUST, 15), schedule.getNextDate(today))

        today = LocalDate.of(2019, Month.NOVEMBER, 15)
        Assertions.assertEquals(today, schedule.getNextDate(today))

        today = LocalDate.of(2019, Month.DECEMBER, 16)
        Assertions.assertEquals(LocalDate.of(2020, Month.JANUARY, 15), schedule.getNextDate(today))
    }

    @Test
    fun monthlyLeapDay() {
        val schedule = MonthlySchedule(29)
        var today = LocalDate.of(2019, Month.FEBRUARY, 28)

        Assertions.assertEquals(today, schedule.getNextDate(today))

        today = LocalDate.of(2019, Month.FEBRUARY, 1)
        Assertions.assertEquals(LocalDate.of(2019, Month.FEBRUARY, 28), schedule.getNextDate(today))

        today = LocalDate.of(2019, Month.JULY, 28)
        Assertions.assertEquals(LocalDate.of(2019, Month.JULY, 29), schedule.getNextDate(today))

        today = LocalDate.of(2020, Month.FEBRUARY, 1)
        Assertions.assertEquals(LocalDate.of(2020, Month.FEBRUARY, 29), schedule.getNextDate(today))
    }
}