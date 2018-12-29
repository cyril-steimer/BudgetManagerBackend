package ch.cyril.budget.manager.backend.systemtest

import ch.cyril.budget.manager.backend.main.ServerType
import ch.cyril.budget.manager.backend.main.startServer
import ch.cyril.budget.manager.backend.model.*
import ch.cyril.budget.manager.backend.rest.lib.RestServer
import ch.cyril.budget.manager.backend.util.SubList
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import java.math.BigDecimal
import java.nio.file.Files
import java.nio.file.Path

class FilebasedExpenseSystemTester(tempDir: Path, server: ServerType, port: Int) : AutoCloseable {

    private val client = HttpClient(port)

    private val expensesContent = """
        Id1,Expense1,200,Budget1,600,Amex,Tag1,Tag2
        Id2,Expense2,300,Budget2,200,,Tag1
        Id3,Expense3,300,Budget1,400,Amex
    """.trimIndent()

    private val expensesFile = Files.write(tempDir.resolve("expenses"), expensesContent.toByteArray())

    private val e1 = Expense(
            Id("Id1"),
            Name("Expense1"),
            Amount(BigDecimal(200)),
            Category("Budget1"),
            Timestamp(600),
            PaymentMethod("Amex"),
            setOf(Tag("Tag1"), Tag("Tag2")))

    private val e2 = Expense(
            Id("Id2"),
            Name("Expense2"),
            Amount(BigDecimal(300)),
            Category("Budget2"),
            Timestamp(200),
            PaymentMethod(""),
            setOf(Tag("Tag1")));

    private val e3 = Expense(
            Id("Id3"),
            Name("Expense3"),
            Amount(BigDecimal(300)),
            Category("Budget1"),
            Timestamp(400),
            PaymentMethod("Amex"),
            emptySet())

    private val newE1 = Expense(
            e1.id,
            e1.name,
            Amount(BigDecimal(500)),
            e1.category,
            e1.date,
            e1.method,
            setOf(Tag("Tag1"), Tag("Tag3")))

    private val e4 = Expense(
            Id("1"),
            Name("Expense4"),
            Amount(BigDecimal(700)),
            Category("Budget2"),
            Timestamp(600),
            PaymentMethod("MasterCard"),
            setOf(Tag("Tag1"), Tag("Tag4")))

    private val restServer: RestServer<*>

    init {
        val budgetFile = Files.createFile(tempDir.resolve("budget"))
        val config = ParamBuilder.fileBased(expensesFile, budgetFile, server, port)
        val configFile = Files.write(tempDir.resolve("config.json"), config.toByteArray())
        val params = arrayOf(configFile.toString())
        restServer = startServer(params)
    }

    fun runExpenseSystemTests() {
        getAllExpenses()
        getAllExpensesSortedByDateDescending()
        getExpenseById()
        getExpensesByAmount()
        getExpensesByCategorySortedByAmountDescending()
        getExpensesByTagSortedByDateAscending()
        getExpensesByPaymentMethod()
        getExpensesByDate()
        getExpensesBySearchTag()
        getExpensesBySearchName()
        getExpensesBySearchAmountAndDateSortedByDate()
        getExpensesBySearchAndPost()
        getExpensesBySearchOrPostSortedByIdDescending()
        updateExpense()
        addExpense()
        deleteExpense()
        updateNotExistingExpense()
        deleteNotExistingExpense()
    }

    override fun close() {
        restServer.close()
    }

    private fun getAllExpenses () {
        val url = "/api/v1/expenses"
        assertEqualList(listOf(e1, e2, e3), client.getJson(url))
    }

    private fun getAllExpensesSortedByDateDescending () {
        val url = "/api/v1/expenses?dir=desc&sort=date"
        assertEqualList(listOf(e1, e3, e2), client.getJson(url))
    }

    private fun getExpenseById () {
        val url = "/api/v1/expenses/field/id/Id3?single=true"
        assertEquals(e3, client.getJson<Expense>(url))
    }

    private fun getExpensesByAmount () {
        val url = "/api/v1/expenses/field/amount/300"
        assertEqualList(listOf(e2, e3),  client.getJson(url))
    }

    private fun getExpensesByCategorySortedByAmountDescending () {
        val url = "/api/v1/expenses/field/category/Budget1?sort=amount&dir=desc"
        assertEqualList(listOf(e3, e1), client.getJson(url))
    }

    private fun getExpensesByTagSortedByDateAscending () {
        val url = "/api/v1/expenses/field/tag/Tag1?sort=date&dir=asc"
        assertEqualList(listOf(e2, e1), client.getJson(url))
    }

    private fun getExpensesByPaymentMethod () {
        val url = "/api/v1/expenses/field/method/Amex"
        assertEqualList(listOf(e1, e3), client.getJson(url))
    }

    private fun getExpensesByDate () {
        val url = "/api/v1/expenses/field/date/200"
        assertEqualList(listOf(e2), client.getJson(url))
    }

    private fun getExpensesBySearchTag () {
        val url = "/api/v1/expenses/search/Tag1"
        assertEqualList(listOf(e1, e2), client.getJson(url))
    }

    private fun getExpensesBySearchName () {
        var url = "/api/v1/expenses/search/Expense"
        assertEqualList(listOf(e1, e2, e3), client.getJson(url))
        url = "/api/v1/expenses/search/Expense1"
        assertEqualList(listOf(e1), client.getJson(url))
    }

    private fun getExpensesBySearchAmountAndDateSortedByDate () {
        val url = "/api/v1/expenses/search/200?sort=date"
        assertEqualList(listOf(e2, e1), client.getJson(url))
    }

    private fun getExpensesBySearchAndPost () {
        val tagQuery = jsonObject("tag", JsonPrimitive("Tag1"))
        val methodQuery = jsonObject("method", JsonPrimitive("Amex"))
        val queries = jsonArray(tagQuery, methodQuery)
        val body = jsonObject("and", queries)
        val url = "/api/v1/expenses/search"
        assertEqualList(listOf(e1), client.postJson(url, body))
    }

    private fun getExpensesBySearchOrPostSortedByIdDescending () {
        val idQuery = jsonObject("id", JsonPrimitive("Id3"))
        val nameQuery = jsonObject("name", JsonPrimitive("Expense2"))
        val queries = jsonArray(idQuery, nameQuery)
        val body = jsonObject("or", queries)
        val url = "/api/v1/expenses/search?sort=id&dir=desc"
        assertEqualList(listOf(e3, e2), client.postJson(url, body))
    }

    private fun updateExpense () {
        val url = "/api/v1/expenses"
        client.put(url, newE1)
        assertEqualList(listOf(newE1, e2, e3), client.getJson(url))
        //TODO Check that the file was written
    }

    private fun addExpense () {
        val url = "/api/v1/expenses"
        client.post(url, e4)
        assertEqualList(listOf(newE1, e2, e3, e4), client.getJson(url))
        //TODO Check that the file was written
    }

    private fun deleteExpense () {
        client.delete("/api/v1/expenses?id=Id1")
        assertEqualList(listOf(e2, e3, e4), client.getJson("/api/v1/expenses"))
        //TODO Check that the file was written
    }

    private fun updateNotExistingExpense () {
        val url = "/api/v1/expenses"
        assertThrows(Exception::class.java) { client.put(url, e1) }
        assertEqualList(listOf(e2, e3, e4), client.getJson(url))
    }

    private fun deleteNotExistingExpense () {
        assertThrows(Exception::class.java) { client.delete("/api/v1/expenses?id=Id1") }
        assertEqualList(listOf(e2, e3, e4), client.getJson("/api/v1/expenses"))
    }

    private fun assertEqualList (expected: List<Expense>, actual: SubList<Expense>) {
        assertEquals(expected.size, actual.count)
        assertEquals(expected, actual.values)
    }

    private fun jsonArray (vararg elems: JsonElement): JsonArray {
        val res = JsonArray()
        for (elem in elems) {
            res.add(elem)
        }
        return res
    }

    private fun jsonObject (key: String, value: JsonElement): JsonObject {
        val res = JsonObject()
        res.add(key, value)
        return res;
    }
}