package ch.cyril.budget.manager.backend.systemtest

import ch.cyril.budget.manager.backend.main.ServerType
import ch.cyril.budget.manager.backend.main.main
import ch.cyril.budget.manager.backend.model.*
import ch.cyril.budget.manager.backend.rest.GSON
import ch.cyril.budget.manager.backend.util.SubList
import com.google.gson.reflect.TypeToken
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import java.math.BigDecimal
import java.nio.file.Files

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class KtorFilebasedSystemTest {

    private val budgetContent = """
        Budget1,900,monthly
        Budget2,yearly,1200,1,2018,1,2019,monthly,1400,1,2019,12,2019
    """.trimIndent()

    private val tempDir = Files.createTempDirectory("systemtest")

    private val budgetFile = Files.write(tempDir.resolve("budgets"), budgetContent.toByteArray())

    private val budget1 = Budget(
            Category("Budget1"),
            listOf(
                BudgetAmount(
                    Amount(BigDecimal(900)),
                    BudgetPeriod.MONTHLY,
                    MonthYear(1, 0),
                    MonthYear(1, 9999))))

    private val newBudget1 = Budget(
            Category("Budget1"),
            listOf(
                BudgetAmount(
                    Amount(BigDecimal(500)),
                    BudgetPeriod.MONTHLY,
                    MonthYear(1, 2018),
                    MonthYear(12, 2018))))

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

    private val newBudget3 = Budget(
            Category("Budget3"),
            listOf(
                BudgetAmount(
                    Amount(BigDecimal(800)),
                    BudgetPeriod.YEARLY,
                    MonthYear(1, 2016),
                    MonthYear(12, 2020))))


    private val port = 9000;

    @BeforeAll
    fun setup () {
        val expensesFile = Files.createFile(tempDir.resolve("expenses"))
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
    @Order(100)
    fun getAllBudgets () {
        val budgets = getJson<SubList<Budget>>("/api/v1/budget")
        assertEquals(2, budgets.count)
        assertEquals(budget1, budgets.values[0])
        assertEquals(budget2, budgets.values[1])
    }

    @Test
    @Order(200)
    fun getBudgetByCategory () {
        var budget: Budget = getJson("/api/v1/budget/category/Budget1")
        assertEquals(budget1, budget)
        budget = getJson("/api/v1/budget/category/Budget2")
        assertEquals(budget2, budget)
    }

    @Test
    @Order(250)
    fun getCategories () {
        val categories = getJson<SubList<Category>>("/api/v1/category")
        assertEquals(2, categories.count)
        assertEquals(budget1.category, categories.values[0])
        assertEquals(budget2.category, categories.values[1])
    }

    @Test
    @Order(300)
    fun updateBudget () {
        put("/api/v1/budget", newBudget1)
        val budget = getJson<Budget>("/api/v1/budget/category/Budget1")
        assertEquals(newBudget1, budget)
        //TODO Check that the fîle was written
    }

    @Test
    @Order(400)
    fun addBudget () {
        post("/api/v1/budget", newBudget3)
        val budget = getJson<Budget>("/api/v1/budget/category/Budget3")
        assertEquals(newBudget3, budget)
        //TODO Check that the file was written
    }

    @Test
    @Order(500)
    fun deleteBudget () {
        delete("/api/v1/budget?category=Budget3")
        //TODO Can we determine the exception more exactly? Or even get the status code?
        assertThrows(Exception::class.java) { getJson<Budget>("/api/v1/budget/category/Budget3") }
        //TODO Check that the file was written
    }

    private fun put (path: String, body: Any) {
        val client = HttpClient()
        runBlocking {
            client.put<Unit>(getUrl(path)) {
                this.body = GSON.toJson(body)
            }
        }
    }

    private fun post (path: String, body: Any) {
        val client = HttpClient()
        runBlocking {
            client.post<Unit>(getUrl(path)) {
                this.body = GSON.toJson(body)
            }
        }
    }

    private fun delete (path: String) {
        val client = HttpClient()
        runBlocking {
            client.delete<Unit>(getUrl(path))
        }
    }

    private inline fun <reified T> getJson (path: String): T {
        val client = HttpClient()
        val type = object: TypeToken<T>() {}.type
        return runBlocking {
            val json = client.get<String>(getUrl(path))
            GSON.fromJson<T>(json, type)
        }
    }

    private fun getUrl (path: String) = "http://127.0.0.1:$port$path"
}