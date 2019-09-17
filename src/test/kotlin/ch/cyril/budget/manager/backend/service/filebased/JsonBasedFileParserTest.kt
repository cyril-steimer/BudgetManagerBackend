package ch.cyril.budget.manager.backend.service.filebased

import ch.cyril.budget.manager.backend.model.*
import ch.cyril.budget.manager.backend.service.filebased.budget.BudgetParser
import ch.cyril.budget.manager.backend.service.filebased.expense.ActualExpenseParser
import com.google.gson.Gson
import com.google.gson.JsonArray
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.nio.file.Files
import java.time.LocalDate

class JsonBasedFileParserTest {

    internal class TempFile() : AutoCloseable {

        val file = Files.createTempFile("", "")

        override fun close() {
            Files.delete(file)
        }
    }

    val expenseJson = """
        [
            {
                "id":"1",
                "name":{
                    "name":"Test"
                },
                "amount":{
                    "amount":50.5
                },
                "category":{
                    "name":"Clothes"
                },
                "date":{
                    "year":2019,
                    "month":8,
                    "day":3
                },
                "method":{
                    "name":"Maestro"
                },
                "author":{
                    "name":"Cyril"
                },
                "tags":[
                    {
                        "name":"H&M"
                    }
                ]
            }
        ]
        """.trimIndent()

    val expense = ActualExpense(
            Id("1"),
            Name("Test"),
            Amount(BigDecimal.valueOf(50.5)),
            Category("Clothes"),
            Timestamp.ofEpochDay(18111),
            PaymentMethod("Maestro"),
            Author("Cyril"),
            setOf(Tag("H&M")))

    val budgetJson = """
        [
            {
                "category":{
                    "name":"Clothes"
                },
                "amounts":[
                    {
                        "amount":{
                            "amount":300
                        },
                        "period":"monthly",
                        "from":{
                            "month":1,
                            "year":2019
                        },
                        "to":{
                            "month":12,
                            "year":2019
                        }
                    }
                ]
            }
        ]
        """.trimIndent()

    val budget = Budget(
            Category("Clothes"),
            listOf(
                BudgetAmount(
                    Amount(BigDecimal.valueOf(300)),
                    BudgetPeriod.MONTHLY,
                    MonthYear(1, 2019),
                    MonthYear(12, 2019))))

    @Test
    fun deserializeExpense() {
        TempFile().use {
            Files.write(it.file, expenseJson.toByteArray(Charsets.UTF_8))
            val actual = ActualExpenseParser().read(it.file)
            Assertions.assertEquals(listOf(expense), actual)
        }
    }

    @Test
    fun serializeExpense() {
        TempFile().use {
            ActualExpenseParser().write(it.file, listOf(expense))
            val actual = Gson().fromJson(Files.newBufferedReader(it.file, Charsets.UTF_8), JsonArray::class.java)
            val expected = Gson().fromJson(expenseJson, JsonArray::class.java)
            Assertions.assertEquals(expected, actual)
        }
    }

    @Test
    fun deserializeBudget() {
        TempFile().use {
            Files.write(it.file, budgetJson.toByteArray(Charsets.UTF_8))
            val actual = BudgetParser().read(it.file)
            Assertions.assertEquals(listOf(budget), actual)
        }
    }

    @Test
    fun serializeBudget() {
        TempFile().use {
            BudgetParser().write(it.file, listOf(budget))
            val actual = Gson().fromJson(Files.newBufferedReader(it.file, Charsets.UTF_8), JsonArray::class.java)
            val expected = Gson().fromJson(budgetJson, JsonArray::class.java)
            Assertions.assertEquals(expected, actual)
        }
    }
}