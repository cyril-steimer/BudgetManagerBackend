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
import org.junit.jupiter.api.Assertions
import java.math.BigDecimal
import java.nio.file.Files
import java.nio.file.Path

class FilebasedBudgetSystemTester(
        private val tempDir: Path,
        private val server: ServerType,
        private val port: Int) : AutoCloseable {

    private val budgetContent = """
        Budget1,900,monthly
        Budget2,yearly,1200,1,2018,1,2019,monthly,1400,1,2019,12,2019
    """.trimIndent()

    private val budgetFile: Path

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

    init {
        budgetFile = Files.write(tempDir.resolve("budgets"), budgetContent.toByteArray())

        val expensesFile = Files.createFile(tempDir.resolve("expenses"))
        val config = ParamBuilder.fileBased(expensesFile, budgetFile, ServerType.KTOR, port)
        val configFile = Files.write(tempDir.resolve("config.json"), config.toByteArray())
        val params = arrayOf(configFile.toString())
        main(params);
    }

    fun runBudgetSystemTests() {
        getAllBudgets()
        getBudgetByCategory()
        getCategories()
        updateBudget()
        addBudget()
        deleteBudget()
        addExistingBudget()
        updateNotExistingBudget()
        deleteNotExistingBudget()
    }

    override fun close() {
        //TODO Destroy the server
    }

    private fun getAllBudgets () {
        val budgets = getJson<SubList<Budget>>("/api/v1/budget")
        Assertions.assertEquals(2, budgets.count)
        Assertions.assertEquals(budget1, budgets.values[0])
        Assertions.assertEquals(budget2, budgets.values[1])
    }

    private fun getBudgetByCategory () {
        var budget: Budget = getJson("/api/v1/budget/category/Budget1")
        Assertions.assertEquals(budget1, budget)
        budget = getJson("/api/v1/budget/category/Budget2")
        Assertions.assertEquals(budget2, budget)
    }

    private fun getCategories () {
        val categories = getJson<SubList<Category>>("/api/v1/category")
        Assertions.assertEquals(2, categories.count)
        Assertions.assertEquals(budget1.category, categories.values[0])
        Assertions.assertEquals(budget2.category, categories.values[1])
    }

    private fun updateBudget () {
        put("/api/v1/budget", newBudget1)
        val budget = getJson<Budget>("/api/v1/budget/category/Budget1")
        Assertions.assertEquals(newBudget1, budget)
        //TODO Check that the fîle was written
    }

    private fun addBudget () {
        post("/api/v1/budget", newBudget3)
        val budget = getJson<Budget>("/api/v1/budget/category/Budget3")
        Assertions.assertEquals(newBudget3, budget)
        //TODO Check that the file was written
    }

    private fun deleteBudget () {
        delete("/api/v1/budget?category=Budget3")
        //TODO Can we determine the exception more exactly? Or even get the status code?
        Assertions.assertThrows(Exception::class.java) { getJson<Budget>("/api/v1/budget/category/Budget3") }
        //TODO Check that the file was written
    }

    private fun addExistingBudget () {
        Assertions.assertThrows(Exception::class.java) { post("/api/v1/budget", budget1) }
        val budget = getJson<Budget>("/api/v1/budget/category/Budget1")
        Assertions.assertEquals(newBudget1, budget)
    }

    private fun updateNotExistingBudget () {
        val budget4 = Budget(Category("Budget4"), budget1.amounts)
        Assertions.assertThrows(Exception::class.java) { put("/api/v1/budget", budget4) }
        Assertions.assertThrows(Exception::class.java) { getJson<Budget>("/api/v1/budget/category/Budget4") }
    }

    private fun deleteNotExistingBudget () {
        Assertions.assertThrows(Exception::class.java) { delete("/api/v1/budget?category=Budget4") }
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