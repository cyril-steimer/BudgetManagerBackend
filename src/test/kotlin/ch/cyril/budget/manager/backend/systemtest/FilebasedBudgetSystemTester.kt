package ch.cyril.budget.manager.backend.systemtest

import ch.cyril.budget.manager.backend.main.ServerType
import ch.cyril.budget.manager.backend.main.main
import ch.cyril.budget.manager.backend.model.*
import ch.cyril.budget.manager.backend.util.SubList
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import java.math.BigDecimal
import java.nio.file.Files
import java.nio.file.Path

class FilebasedBudgetSystemTester(
        private val tempDir: Path,
        server: ServerType,
        port: Int) : AutoCloseable {

    private val client = HttpClient(port)

    private val budgetContent = """
        Budget1,900,monthly
        Budget2,yearly,1200,1,2018,1,2019,monthly,1400,1,2019,12,2019
    """.trimIndent()

    private val budgetFile = Files.write(tempDir.resolve("budget"), budgetContent.toByteArray())

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
        val expensesFile = Files.createFile(tempDir.resolve("expenses"))
        val config = ParamBuilder.fileBased(expensesFile, budgetFile, server, port)
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
        val budgets = client.getJson<SubList<Budget>>("/api/v1/budget")
        assertEquals(2, budgets.count)
        assertEquals(listOf(budget1, budget2), budgets.values)
    }

    private fun getBudgetByCategory () {
        var budget: Budget = client.getJson("/api/v1/budget/category/Budget1")
        assertEquals(budget1, budget)
        budget = client.getJson("/api/v1/budget/category/Budget2")
        assertEquals(budget2, budget)
    }

    private fun getCategories () {
        val categories = client.getJson<SubList<Category>>("/api/v1/category")
        assertEquals(2, categories.count)
        assertEquals(budget1.category, categories.values[0])
        assertEquals(budget2.category, categories.values[1])
    }

    private fun updateBudget () {
        client.put("/api/v1/budget", newBudget1)
        val budget = client.getJson<Budget>("/api/v1/budget/category/Budget1")
        assertEquals(newBudget1, budget)
        //TODO Check that the fîle was written
    }

    private fun addBudget () {
        client.post("/api/v1/budget", newBudget3)
        val budget = client.getJson<Budget>("/api/v1/budget/category/Budget3")
        assertEquals(newBudget3, budget)
        //TODO Check that the file was written
    }

    private fun deleteBudget () {
        client.delete("/api/v1/budget?category=Budget3")
        //TODO Can we determine the exception more exactly? Or even get the status code?
        assertThrows(Exception::class.java) { client.getJson<Budget>("/api/v1/budget/category/Budget3") }
        //TODO Check that the file was written
    }

    private fun addExistingBudget () {
        assertThrows(Exception::class.java) { client.post("/api/v1/budget", budget1) }
        val budget = client.getJson<Budget>("/api/v1/budget/category/Budget1")
        assertEquals(newBudget1, budget)
    }

    private fun updateNotExistingBudget () {
        val budget4 = Budget(Category("Budget4"), budget1.amounts)
        assertThrows(Exception::class.java) { client.put("/api/v1/budget", budget4) }
        assertThrows(Exception::class.java) { client.getJson<Budget>("/api/v1/budget/category/Budget4") }
    }

    private fun deleteNotExistingBudget () {
        assertThrows(Exception::class.java) { client.delete("/api/v1/budget?category=Budget4") }
    }
}