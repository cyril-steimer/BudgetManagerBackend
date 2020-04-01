package ch.cyril.budget.manager.backend.systemtest

import ch.cyril.budget.manager.backend.main.ServerType
import ch.cyril.budget.manager.backend.main.startServer
import ch.cyril.budget.manager.backend.model.*
import ch.cyril.budget.manager.backend.rest.lib.RestServer
import ch.cyril.budget.manager.backend.service.filebased.budget.BudgetParser
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

    private val budget1 = Budget(
            Id("1"),
            Category("Budget1"),
            listOf(
                    BudgetAmount(
                            Amount(BigDecimal(900)),
                            BudgetPeriod.MONTHLY,
                            MonthYear(1, 0),
                            MonthYear(1, 9999))))

    private val newBudget1 = Budget(
            Id("1"),
            Category("Budget1"),
            listOf(
                    BudgetAmount(
                            Amount(BigDecimal(500)),
                            BudgetPeriod.MONTHLY,
                            MonthYear(1, 2018),
                            MonthYear(12, 2018))))

    private val budget2 = Budget(
            Id("2"),
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
            Id("3"),
            Category("Budget3"),
            listOf(
                    BudgetAmount(
                            Amount(BigDecimal(800)),
                            BudgetPeriod.YEARLY,
                            MonthYear(1, 2016),
                            MonthYear(12, 2020))))

    private val budgetFile: Path

    private val restServer: RestServer<*>

    init {
        val expensesFile = Files.createFile(tempDir.resolve("expenses"))
        val templateFile = Files.createFile(tempDir.resolve("templates"))
        val scheduleFile = Files.createFile(tempDir.resolve("schedules"))
        budgetFile = tempDir.resolve("budget")
        BudgetParser().write(budgetFile, listOf(budget1, budget2))
        val config = ParamBuilder.fileBased(expensesFile, templateFile, scheduleFile, budgetFile, server, port)
        val configFile = Files.write(tempDir.resolve("config.json"), config.toByteArray())
        val params = arrayOf(configFile.toString())
        restServer = startServer(params)
    }

    fun runBudgetSystemTests() {
        getAllBudgets()
        getBudgetById()
        getBudgetByCategory()
        updateBudget()
        addBudget()
        updateBudgetCategory()
        deleteBudget()
        updateNotExistingBudget()
        deleteNotExistingBudget()
        updateBudgetCategoryToExistingName()
    }

    override fun close() {
        restServer.close()
    }

    private fun getAllBudgets() {
        val budgets = client.getJson<SubList<Budget>>("/api/v1/budget")
        assertEquals(2, budgets.count)
        assertEquals(listOf(budget1, budget2), budgets.values)
    }

    private fun getBudgetById() {
        var budget: Budget = client.getJson("/api/v1/budget/id/1")
        assertEquals(budget1, budget)
        budget = client.getJson("/api/v1/budget/id/2")
        assertEquals(budget2, budget)
    }

    private fun getBudgetByCategory() {
        var budget: Budget = client.getJson("/api/v1/budget/category/Budget1")
        assertEquals(budget1, budget)
        budget = client.getJson("/api/v1/budget/category/Budget2")
        assertEquals(budget2, budget)
    }

    private fun updateBudget() {
        client.put("/api/v1/budget", newBudget1)
        val budget = client.getJson<Budget>("/api/v1/budget/id/1")
        assertEquals(newBudget1, budget)
        //TODO Check that the fîle was written
    }

    private fun addBudget() {
        client.post("/api/v1/budget", newBudget3)
        val budget = client.getJson<Budget>("/api/v1/budget/id/3")
        assertEquals(newBudget3, budget)
        //TODO Check that the file was written
    }

    private fun updateBudgetCategory() {
        val budget3WithNewCategory = newBudget3.copy(category = Category("This is another category"))
        client.put("/api/v1/budget", budget3WithNewCategory)
        val budget = client.getJson<Budget>("/api/v1/budget/id/3")
        assertEquals(budget3WithNewCategory, budget)
    }

    private fun deleteBudget() {
        client.delete("/api/v1/budget?id=3")
        //TODO Can we determine the exception more exactly? Or even get the status code?
        assertThrows(Exception::class.java) { client.getJson<Budget>("/api/v1/budget/id/3") }
        //TODO Check that the file was written
    }

    private fun updateNotExistingBudget() {
        val budget4 = Budget(Id("4"), Category("Budget4"), budget1.amounts)
        assertThrows(Exception::class.java) { client.put("/api/v1/budget", budget4) }
        assertThrows(Exception::class.java) { client.getJson<Budget>("/api/v1/budget/id/4") }
    }

    private fun deleteNotExistingBudget() {
        assertThrows(Exception::class.java) { client.delete("/api/v1/budget?id=4") }
    }

    private fun updateBudgetCategoryToExistingName() {
        assertThrows(java.lang.Exception::class.java) { client.put("/api/v1/budget", budget1.copy(category = Category("Budget2")))}
    }
}