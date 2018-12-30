package ch.cyril.budget.manager.backend.rest

import ch.cyril.budget.manager.backend.model.Expense
import ch.cyril.budget.manager.backend.model.ExpenseWithoutId
import ch.cyril.budget.manager.backend.model.Id
import ch.cyril.budget.manager.backend.rest.lib.*
import ch.cyril.budget.manager.backend.service.Pagination
import ch.cyril.budget.manager.backend.service.expense.*
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import java.util.*

class ExpenseRestHandler(private val expenseDao: ExpenseDao) {

    @HttpMethod(HttpVerb.POST, "/api/v1/expenses/search")
    fun search(
            @Body body: JsonObject,
            @QueryParam("sort") field: ExpenseSortField?,
            @QueryParam("dir") dir: SortDirection?,
            @QueryParam("from") from: Int?,
            @QueryParam("count") count: Int?,
            @QueryParam("single") single: Boolean?): RestResult {

        val query = ExpenseQueryDescriptor.createQuery(body)
        return handleQuery(query, ExpenseSortField.sort(field, dir), Pagination.of(from, count), single)
    }

    @HttpMethod(HttpVerb.POST, "/api/v1/expenses/search/:filter")
    fun search(
            @PathParam("filter") filter: String,
            @Body body: JsonObject,
            @QueryParam("sort") field: ExpenseSortField?,
            @QueryParam("dir") dir: SortDirection?,
            @QueryParam("from") from: Int?,
            @QueryParam("count") count: Int?,
            @QueryParam("single") single: Boolean?): RestResult {

        val filterQuery = filterQuery(filter)
        val searchQuery = ExpenseQueryDescriptor.createQuery(body)
        val query = AndExpenseQuery(listOf(filterQuery, searchQuery))
        return handleQuery(query, ExpenseSortField.sort(field, dir), Pagination.of(from, count), single)
    }

    @HttpMethod(HttpVerb.GET, "/api/v1/expenses/search/:filter")
    fun search(
            @PathParam("filter") filter: String,
            @QueryParam("sort") field: ExpenseSortField?,
            @QueryParam("dir") dir: SortDirection?,
            @QueryParam("from") from: Int?,
            @QueryParam("count") count: Int?,
            @QueryParam("single") single: Boolean?): RestResult {

        val query = filterQuery(filter)
        return handleQuery(query, ExpenseSortField.sort(field, dir), Pagination.of(from, count), single)
    }

    @HttpMethod(HttpVerb.GET, "/api/v1/expenses/field/:query/:arg")
    fun simpleQuery(
            @PathParam("query") desc: SimpleExpenseQueryDescriptor,
            @PathParam("arg") arg: String,
            @QueryParam("sort") field: ExpenseSortField?,
            @QueryParam("dir") dir: SortDirection?,
            @QueryParam("from") from: Int?,
            @QueryParam("count") count: Int?,
            @QueryParam("single") single: Boolean?): RestResult {

        val query = desc.createQuery(JsonPrimitive(arg))
        return handleQuery(query, ExpenseSortField.sort(field, dir), Pagination.of(from, count), single)
    }

    @HttpMethod(HttpVerb.GET, "/api/v1/expenses")
    fun getAllExpenses(
            @QueryParam("sort") field: ExpenseSortField?,
            @QueryParam("dir") dir: SortDirection?,
            @QueryParam("from") from: Int?,
            @QueryParam("count") count: Int?): RestResult {

        return handleQuery(null, ExpenseSortField.sort(field, dir), Pagination.of(from, count), false)
    }

    @HttpMethod(HttpVerb.PUT, "/api/v1/expenses")
    fun updateExpense(@Body expense: Expense) {
        expenseDao.updateExpense(expense)
    }

    @HttpMethod(HttpVerb.POST, "/api/v1/expenses")
    fun addExpense(@Body expense: ExpenseWithoutId) {
        expenseDao.addExpense(expense)
    }

    @HttpMethod(HttpVerb.DELETE, "/api/v1/expenses")
    fun deleteExpense(@QueryParam("id") id: String) {
        expenseDao.deleteExpense(Id(id))
    }

    @HttpMethod(HttpVerb.GET, "/api/v1/paymentmethod")
    fun getPaymentMethods(): RestResult {
        val res = GSON.toJson(expenseDao.getPaymentMethods())
        return RestResult.json(res)
    }

    @HttpMethod(HttpVerb.GET, "/api/v1/tag")
    fun getTags(): RestResult {
        val res = GSON.toJson(expenseDao.getTags())
        return RestResult.json(res)
    }

    @HttpMethod(HttpVerb.GET, "/api/v1/author")
    fun getAuthors(): RestResult {
        val res = GSON.toJson(expenseDao.getAuthors())
        return RestResult.json(res)
    }

    private fun filterQuery(filter: String): ExpenseQuery {
        val queries = LinkedList<ExpenseQuery>()
        for (desc in SimpleExpenseQueryDescriptor.values()) {
            try {
                queries.add(desc.createQuery(JsonPrimitive(filter)))
            } catch (e: Exception) {
                //Ignore, can happen if e.g. arg is not int
            }
        }
        return OrExpenseQuery(queries)
    }

    private fun handleQuery(
            query: ExpenseQuery?,
            sort: ExpenseSort?,
            pagination: Pagination?,
            single: Boolean?): RestResult {

        if (single == true) {
            return getOne(query, sort)
        }
        return getMany(query, sort, pagination)
    }

    private fun getOne(
            query: ExpenseQuery?,
            sort: ExpenseSort?): RestResult {

        val res = expenseDao.getOneExpense(query, sort) ?: throw IllegalArgumentException("No result found")
        val json = GSON.toJson(res)
        return RestResult.json(json)
    }

    private fun getMany(
            query: ExpenseQuery?,
            sort: ExpenseSort?,
            pagination: Pagination?): RestResult {

        val res = expenseDao.getExpenses(query, sort, pagination)
        val json = GSON.toJson(res)
        return RestResult.json(json)
    }
}