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

class BulkUpdateGson(val query: JsonObject?, val update: JsonObject)

class ExpenseRestHandler(private val dao: ExpenseDao, private val path: String) : RestHandler {

    override fun getHandlerMethods(): List<RestHandlerMethod> {
        return listOf(
                RestHandlerMethod(::search, HttpVerb.POST, "/api/v1/${path}/search"),
                RestHandlerMethod(::searchAndFilter, HttpVerb.POST, "/api/v1/${path}/search/:filter"),
                RestHandlerMethod(::filter, HttpVerb.GET, "/api/v1/${path}/search/:filter"),
                RestHandlerMethod(::simpleQuery, HttpVerb.GET, "/api/v1/${path}/field/:query/:arg"),
                RestHandlerMethod(::getAllExpenses, HttpVerb.GET, "/api/v1/${path}"),
                RestHandlerMethod(::updateExpense, HttpVerb.PUT, "/api/v1/${path}"),
                RestHandlerMethod(::addExpense, HttpVerb.POST, "/api/v1/${path}"),
                RestHandlerMethod(::deleteExpense, HttpVerb.DELETE, "/api/v1/${path}"),
                RestHandlerMethod(::bulkUpdate, HttpVerb.PUT, "/api/v1/${path}/bulk"))
    }

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

    fun searchAndFilter(
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

    fun filter(
            @PathParam("filter") filter: String,
            @QueryParam("sort") field: ExpenseSortField?,
            @QueryParam("dir") dir: SortDirection?,
            @QueryParam("from") from: Int?,
            @QueryParam("count") count: Int?,
            @QueryParam("single") single: Boolean?): RestResult {

        val query = filterQuery(filter)
        return handleQuery(query, ExpenseSortField.sort(field, dir), Pagination.of(from, count), single)
    }

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
    fun getAllExpenses(
            @QueryParam("sort") field: ExpenseSortField?,
            @QueryParam("dir") dir: SortDirection?,
            @QueryParam("from") from: Int?,
            @QueryParam("count") count: Int?): RestResult {

        return handleQuery(null, ExpenseSortField.sort(field, dir), Pagination.of(from, count), false)
    }

    fun updateExpense(@Body expense: Expense) {
        dao.updateExpense(expense)
    }

    fun addExpense(@Body expense: ExpenseWithoutId) {
        dao.addExpense(expense)
    }

    fun deleteExpense(@QueryParam("id") id: String) {
        dao.deleteExpense(Id(id))
    }

    fun bulkUpdate(@Body update: BulkUpdateGson) {
        val query = if (update.query != null) ExpenseQueryDescriptor.createQuery(update.query) else null
        val update = ExpenseUpdateDescriptor.createUpdate(update.update)
        dao.applyBulkUpdate(query, update)
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

        val res = dao.getOneExpense(query, sort) ?: throw IllegalArgumentException("No result found")
        val json = GSON.toJson(res)
        return RestResult.json(json)
    }

    private fun getMany(
            query: ExpenseQuery?,
            sort: ExpenseSort?,
            pagination: Pagination?): RestResult {

        val res = dao.getExpenses(query, sort, pagination)
        val json = GSON.toJson(res)
        return RestResult.json(json)
    }
}