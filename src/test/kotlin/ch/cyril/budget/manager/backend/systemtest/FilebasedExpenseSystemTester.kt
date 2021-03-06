package ch.cyril.budget.manager.backend.systemtest

import ch.cyril.budget.manager.backend.main.ServerType
import ch.cyril.budget.manager.backend.main.startServer
import ch.cyril.budget.manager.backend.model.*
import ch.cyril.budget.manager.backend.rest.lib.RestServer
import ch.cyril.budget.manager.backend.service.filebased.budget.BudgetParser
import ch.cyril.budget.manager.backend.service.filebased.budget.FilebasedBudgetDao
import ch.cyril.budget.manager.backend.service.filebased.expense.ActualExpenseParser
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

    private val b1 = Budget(Id("1"), Category("Budget1"), emptyList())

    private val b2 = Budget(Id("2"), Category("Budget2"), emptyList())

    private val e1 = ActualExpense(
            Id("Id1"),
            Name("Expense1"),
            Amount(BigDecimal(200)),
            b1,
            Timestamp.ofEpochDay(2),
            PaymentMethod("Amex"),
            Author(""),
            setOf(Tag("Tag1"), Tag("Tag2")))

    private val e2 = ActualExpense(
            Id("Id2"),
            Name("Expense2"),
            Amount(BigDecimal(300)),
            b2,
            Timestamp.ofEpochDay(0),
            PaymentMethod(""),
            Author(""),
            setOf(Tag("Tag1"), Tag("200")));

    private val e3 = ActualExpense(
            Id("Id3"),
            Name("Expense3"),
            Amount(BigDecimal(300)),
            b1,
            Timestamp.ofEpochDay(1),
            PaymentMethod("Amex"),
            Author("Cyril"),
            emptySet())

    private val newE1 = e1.copy(amount = Amount(BigDecimal(500)), tags = setOf(Tag("Tag1"), Tag("Tag3")))

    private val expensesFile: Path

    private val e4 = ActualExpense(
            Id("1"),
            Name("Expense4"),
            Amount(BigDecimal(700)),
            b2,
            Timestamp.ofEpochDay(2),
            PaymentMethod("MasterCard"),
            Author("Diana"),
            setOf(Tag("Tag1"), Tag("Tag4")))

    private val newAuthor = "New Author"

    private val restServer: RestServer<*>

    init {
        val budgetFile = Files.createFile(tempDir.resolve("budget"))
        val templatesFile = Files.createFile(tempDir.resolve("templates"))
        val scheduleFile = Files.createFile(tempDir.resolve("schedules"))
        expensesFile = tempDir.resolve("expenses")
        BudgetParser().write(budgetFile, listOf(b1, b2))
        ActualExpenseParser(FilebasedBudgetDao(budgetFile)).write(expensesFile, listOf(e1, e2, e3))
        val config = ParamBuilder.fileBased(expensesFile, templatesFile, scheduleFile, budgetFile, server, port)
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
        getExpensesByAuthor()
        getExpensesBySearchTag()
        getExpensesBySearchDate()
        getExpensesBySearchName()
        getExpensesBySearchAmountAndTag()
        getExpensesBySearchUsingPost()
        getExpensesBySearchOrPostSortedByIdDescending()
        getTags()
        getPaymentMethods()
        getAuthors()
        updateExpense()
        addExpense()
        deleteExpense()
        updateNotExistingExpense()
        deleteNotExistingExpense()
        bulkUpdateAuthorNoQuery()
        bulkUpdateAuthorWithQuery()
        updateBudgetCategory()
    }

    override fun close() {
        restServer.close()
    }

    private fun getAllExpenses() {
        val url = "/api/v1/expenses"
        assertEqualList(listOf(e1, e2, e3), client.getJson(url))
    }

    private fun getAllExpensesSortedByDateDescending() {
        val url = "/api/v1/expenses?dir=desc&sort=date"
        assertEqualList(listOf(e1, e3, e2), client.getJson(url))
    }

    private fun getExpenseById() {
        val url = "/api/v1/expenses/field/id/Id3?single=true"
        assertEquals(e3, client.getJson<ActualExpense>(url))
    }

    private fun getExpensesByAmount() {
        val url = "/api/v1/expenses/field/amount/300"
        assertEqualList(listOf(e2, e3), client.getJson(url))
    }

    private fun getExpensesByCategorySortedByAmountDescending() {
        val url = "/api/v1/expenses/field/category/Budget1?sort=amount&dir=desc"
        assertEqualList(listOf(e3, e1), client.getJson(url))
    }

    private fun getExpensesByTagSortedByDateAscending() {
        val url = "/api/v1/expenses/field/tag/Tag1?sort=date&dir=asc"
        assertEqualList(listOf(e2, e1), client.getJson(url))
    }

    private fun getExpensesByPaymentMethod() {
        val url = "/api/v1/expenses/field/method/Amex"
        assertEqualList(listOf(e1, e3), client.getJson(url))
    }

    private fun getExpensesByDate() {
        val url = "/api/v1/expenses/field/date/1970-01-01"
        assertEqualList(listOf(e2), client.getJson(url))
    }

    private fun getExpensesByAuthor() {
        val url = "/api/v1/expenses/field/author/Cyril"
        assertEqualList(listOf(e3), client.getJson(url))
    }

    private fun getExpensesBySearchTag() {
        val url = "/api/v1/expenses/search/Tag1"
        assertEqualList(listOf(e1, e2), client.getJson(url))
    }

    private fun getExpensesBySearchDate() {
        val url = "/api/v1/expenses/search/1970-01-03"
        assertEqualList(listOf(e1), client.getJson(url))
    }

    private fun getExpensesBySearchName() {
        var url = "/api/v1/expenses/search/Expense"
        assertEqualList(listOf(e1, e2, e3), client.getJson(url))
        url = "/api/v1/expenses/search/Expense1"
        assertEqualList(listOf(e1), client.getJson(url))
    }

    private fun getExpensesBySearchAmountAndTag() {
        val url = "/api/v1/expenses/search/200"
        assertEqualList(listOf(e1, e2), client.getJson(url))
    }

    private fun getExpensesBySearchUsingPost() {
        val tagQuery = jsonObject("tag", JsonPrimitive("Tag1"))
        val methodQuery = jsonObject("method", JsonPrimitive("Amex"))
        val queries = jsonArray(tagQuery, methodQuery)
        val body = jsonObject("and", queries)
        val url = "/api/v1/expenses/search"
        assertEqualList(listOf(e1), client.postJson(url, body))
    }

    private fun getExpensesBySearchOrPostSortedByIdDescending() {
        val idQuery = jsonObject("id", JsonPrimitive("Id3"))
        val nameQuery = jsonObject("name", JsonPrimitive("Expense2"))
        val queries = jsonArray(idQuery, nameQuery)
        val body = jsonObject("or", queries)
        val url = "/api/v1/expenses/search?sort=id&dir=desc"
        assertEqualList(listOf(e3, e2), client.postJson(url, body))
    }

    private fun getTags() {
        val url = "/api/v1/tag"
        assertEquals(setOf(Tag("Tag1"), Tag("Tag2"), Tag("200")), client.getJson<Set<Tag>>(url))
    }

    private fun getPaymentMethods() {
        val url = "/api/v1/paymentmethod"
        assertEquals(setOf(e1.method), client.getJson<Set<PaymentMethod>>(url))
    }

    private fun getAuthors() {
        val url = "/api/v1/author"
        assertEquals(setOf(e3.author), client.getJson<Set<Author>>(url))
    }

    private fun updateExpense() {
        val url = "/api/v1/expenses"
        client.put(url, newE1)
        assertEqualList(listOf(newE1, e2, e3), client.getJson(url))
        //TODO Check that the file was written
    }

    private fun addExpense() {
        val url = "/api/v1/expenses"
        client.post(url, e4)
        assertEqualList(listOf(newE1, e2, e3, e4), client.getJson(url))
        //TODO Check that the file was written
    }

    private fun deleteExpense() {
        client.delete("/api/v1/expenses?id=Id1")
        assertEqualList(listOf(e2, e3, e4), client.getJson("/api/v1/expenses"))
        //TODO Check that the file was written
    }

    private fun updateNotExistingExpense() {
        val url = "/api/v1/expenses"
        assertThrows(Exception::class.java) { client.put(url, e1) }
        assertEqualList(listOf(e2, e3, e4), client.getJson(url))
    }

    private fun deleteNotExistingExpense() {
        assertThrows(Exception::class.java) { client.delete("/api/v1/expenses?id=Id1") }
        assertEqualList(listOf(e2, e3, e4), client.getJson("/api/v1/expenses"))
    }

    private fun bulkUpdateAuthorNoQuery() {
        val update = jsonObject("author", JsonPrimitive(newAuthor))
        val bulk = jsonObject("update", update)
        client.put("/api/v1/expenses/bulk", bulk)
        assertEqualList(
                listOf(withAuthor(e2, newAuthor), withAuthor(e3, newAuthor), withAuthor(e4, newAuthor)),
                client.getJson("/api/v1/expenses"))
        //TODO Check that the file was written
    }

    private fun bulkUpdateAuthorWithQuery() {
        val query = jsonObject("name", JsonPrimitive("Expense2"))
        val update = jsonObject("author", JsonPrimitive("XYZ"))
        val bulk = jsonObject("query", query)
        bulk.add("update", update)
        client.put("/api/v1/expenses/bulk", bulk)
        assertEqualList(
                listOf(withAuthor(e2, "XYZ"), withAuthor(e3, newAuthor), withAuthor(e4, newAuthor)),
                client.getJson("/api/v1/expenses"))
        //TODO Check that the file was written
    }

    private fun updateBudgetCategory() {
        client.put("/api/v1/budget", b2.copy(category = Category("New Budget Id")))
        val expense = client.getJson<ActualExpense>("/api/v1/expenses/field/id/1?single=true")
        assertEquals(Category("New Budget Id"), expense.budget?.category)
    }

    private fun withAuthor(expense: ActualExpense, author: String) = expense.copy(author = Author(author))

    private fun assertEqualList(expected: List<ActualExpense>, actual: SubList<ActualExpense>) {
        assertEquals(expected.size, actual.count)
        assertEquals(expected, actual.values)
    }

    private fun jsonArray(vararg elems: JsonElement): JsonArray {
        val res = JsonArray()
        for (elem in elems) {
            res.add(elem)
        }
        return res
    }

    private fun jsonObject(key: String, value: JsonElement): JsonObject {
        val res = JsonObject()
        res.add(key, value)
        return res;
    }
}