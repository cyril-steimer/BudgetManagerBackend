package ch.cyril.budget.manager.backend.systemtest

import ch.cyril.budget.manager.backend.main.ServerType
import ch.cyril.budget.manager.backend.main.main
import ch.cyril.budget.manager.backend.model.*
import ch.cyril.budget.manager.backend.rest.GSON
import ch.cyril.budget.manager.backend.util.SubList
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import java.math.BigDecimal
import java.nio.file.Files

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KtorFilebasedSystemTest {

    inline fun <reified T> Gson.fromJson(json: String) = this.fromJson<T>(json, object: TypeToken<T>() {}.type)

    private val budgetContent = """
        Budget1,900,monthly
        Budget2,yearly,1200,1,2018,1,2019,monthly,1400,1,2019,12,2019
    """.trimIndent()

    private val budget1 = Budget(
            Category("Budget1"),
            listOf(BudgetAmount(
                    Amount(BigDecimal(900)),
                    BudgetPeriod.MONTHLY,
                    MonthYear(1, 0),
                    MonthYear(1, 9999))))

    private val budget2 = Budget(
            Category("Budget2"),
            listOf(
                BudgetAmount(
                    Amount(BigDecimal(1200)),
                    BudgetPeriod.YEARLY,
                    MonthYear(1, 2018),
                    MonthYear(1, 2019)),
                BudgetAmount(
                    Amount(BigDecimal(1400)),
                    BudgetPeriod.MONTHLY,
                    MonthYear(1, 2019),
                    MonthYear(12, 2019))));

    private val port = 9000;

    @BeforeAll
    fun setup () {
        val tempDir = Files.createTempDirectory("systemtest")
        val expensesFile = Files.createFile(tempDir.resolve("expenses"))
        val budgetFile = Files.write(tempDir.resolve("budgets"), budgetContent.toByteArray())
        val config = ParamBuilder.fileBased(expensesFile, budgetFile, ServerType.KTOR, port)
        val configFile = Files.write(tempDir.resolve("config.json"), config.toByteArray())
        val params = arrayOf(configFile.toString())
        main(params)
    }

    @AfterAll
    fun destroy () {
        //TODO Destroy the server..
    }

    @Test
    fun getAllBudgets () {
        val budgets = getJson<SubList<Budget>>("/api/v1/budget")
        assertEquals(2, budgets.count)
        assertEquals(budget1, budgets.values[0])
        assertEquals(budget2, budgets.values[1])
    }

    @Test
    fun getBudgetByCategory () {
        var budget: Budget = getJson("/api/v1/budget/category/Budget1")
        assertEquals(budget1, budget)
        budget = getJson("/api/v1/budget/category/Budget2")
        assertEquals(budget2, budget)
    }

    private inline fun <reified T> getJson (uri: String): T {
        val client = HttpClient()
        return runBlocking {
            val json = client.get<String>("http://127.0.0.1:$port$uri")
            GSON.fromJson<T>(json)
        }
    }
}