package ch.cyril.budget.manager.backend.rest

import ch.cyril.budget.manager.backend.model.Expense
import ch.cyril.budget.manager.backend.rest.lib.*
import ch.cyril.budget.manager.backend.service.Pagination
import ch.cyril.budget.manager.backend.service.SortDirection
import ch.cyril.budget.manager.backend.service.expense.*
import com.google.gson.JsonPrimitive
import java.util.*

class ExpenseRestHandler(private val expenseDao: ExpenseDao) {

    @HttpMethod(HttpVerb.GET, "/api/v1/expenses/search/:arg")
    fun search(
            @PathParam("arg") arg: String,
            @QueryParam("sort") field: ExpenseSortField?,
            @QueryParam("dir") dir: SortDirection?,
            @QueryParam("from") from: Int?,
            @QueryParam("count") count: Int?,
            @QueryParam("single") single: Boolean?): RestResult {

        val queries = LinkedList<ExpenseQuery>()
        for (desc in SimpleExpenseQueryDescriptor.values()) {
            try {
                queries.add(desc.createQuery(JsonPrimitive(arg)))
            } catch (e: Exception) {
                //Ignore, can happen if e.g. arg is not int
            }
        }
        val query = OrExpenseQuery(queries)
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
            @QueryParam("count") count: Int?,
            @QueryParam("single") single: Boolean?): RestResult {

        return handleQuery(null, ExpenseSortField.sort(field, dir), Pagination.of(from, count), single)
    }

    @HttpMethod(HttpVerb.PUT, "/api/v1/expenses")
    fun updateExpense(@Body expense: Expense) {
        expenseDao.updateExpense(expense)
    }

    @HttpMethod(HttpVerb.POST, "/api/v1/expenses")
    fun addExpense(@Body expense: Expense) {
        expenseDao.addExpense(expense)
    }

    @HttpMethod(HttpVerb.DELETE, "/api/v1/expenses")
    fun deleteExpense(@Body expense: Expense) {
        expenseDao.deleteExpense(expense)
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