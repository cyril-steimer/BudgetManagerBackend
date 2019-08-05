package ch.cyril.budget.manager.backend.model

import ch.cyril.budget.manager.backend.util.gson.AnnotatedTypeAdapterFactory
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month

class ScheduleTest {

    private val gson = GsonBuilder()
            .registerTypeAdapterFactory(AnnotatedTypeAdapterFactory())
            .create()

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

    @Test
    fun serialize() {
        var actual = gson.toJsonTree(WeeklySchedule(DayOfWeek.FRIDAY))

        var expected = JsonObject()
        expected.addProperty("dayOfWeek", "FRIDAY")
        Assertions.assertEquals(expected, actual)

        actual = gson.toJsonTree(MonthlySchedule(20))

        expected = JsonObject()
        expected.addProperty("dayOfMonth", 20)

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun deserialize() {
        var json = """{"dayOfWeek": "MONDAY"}"""
        var actual = gson.fromJson(json, Schedule::class.java)

        var expected : Schedule = WeeklySchedule(DayOfWeek.MONDAY)
        Assertions.assertEquals(expected, actual)

        json = """{"dayOfMonth": 30}"""
        actual = gson.fromJson(json, Schedule::class.java)

        expected = MonthlySchedule(30)
        Assertions.assertEquals(expected, actual)
    }
}