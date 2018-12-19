package ch.cyril.budget.manager.backend.systemtest

import ch.cyril.budget.manager.backend.main.ServerType
import ch.cyril.budget.manager.backend.main.main
import ch.cyril.budget.manager.backend.model.*
import ch.cyril.budget.manager.backend.util.SubList
import org.junit.jupiter.api.Assertions.assertEquals
import java.math.BigDecimal
import java.nio.file.Files
import java.nio.file.Path

class FilebasedExpenseSystemTester(
        private val tempDir: Path,
        server: ServerType,
        port: Int) : AutoCloseable {

    private val client = HttpClient(port)

    private val expensesContent = """
        Id1,Expense1,200,Budget1,100,Amex,Tag1,Tag2
        Id2,Expense2,300,Budget2,300,,Tag1
        Id3,Expense3,400,Budget1,500,Amex
    """.trimIndent()

    private val expensesFile = Files.write(tempDir.resolve("expenses"), expensesContent.toByteArray())

    private val expense1 = Expense(
            Id("Id1"),
            Name("Expense1"),
            Amount(BigDecimal(200)),
            Category("Budget1"),
            Timestamp(100),
            PaymentMethod("Amex"),
            setOf(Tag("Tag1"), Tag("Tag2")))

    private val expense2 = Expense(
            Id("Id2"),
            Name("Expense2"),
            Amount(BigDecimal(300)),
            Category("Budget2"),
            Timestamp(300),
            PaymentMethod(""),
            setOf(Tag("Tag1")));

    private val expense3 = Expense(
            Id("Id3"),
            Name("Expense3"),
            Amount(BigDecimal(400)),
            Category("Budget1"),
            Timestamp(500),
            PaymentMethod("Amex"),
            emptySet())

    init {
        val budgetFile = Files.createFile(tempDir.resolve("budget"))
        val config = ParamBuilder.fileBased(expensesFile, budgetFile, server, port)
        val configFile = Files.write(tempDir.resolve("config.json"), config.toByteArray())
        val params = arrayOf(configFile.toString())
        main(params);
    }

    fun runExpenseSystemTests() {

    }

    override fun close() {
        getAllExpenses()
    }

    private fun getAllExpenses () {
        val expenses = client.getJson<SubList<Expense>>("/api/v1/expenses")
        assertEquals(3, expenses.count)
        assertEquals(listOf(expense1, expense2, expense3), expenses.values)
    }
}