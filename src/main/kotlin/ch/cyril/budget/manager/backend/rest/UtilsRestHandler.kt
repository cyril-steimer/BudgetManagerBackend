package ch.cyril.budget.manager.backend.rest

import ch.cyril.budget.manager.backend.rest.lib.HttpMethod
import ch.cyril.budget.manager.backend.rest.lib.HttpVerb
import ch.cyril.budget.manager.backend.rest.lib.RestResult
import ch.cyril.budget.manager.backend.service.expense.ExpenseDao

class UtilsRestHandler(private val dao: ExpenseDao) {


    @HttpMethod(HttpVerb.GET, "/api/v1/paymentmethod")
    fun getPaymentMethods(): RestResult {
        val res = GSON.toJson(dao.getPaymentMethods())
        return RestResult.json(res)
    }

    @HttpMethod(HttpVerb.GET, "/api/v1/tag")
    fun getTags(): RestResult {
        val res = GSON.toJson(dao.getTags())
        return RestResult.json(res)
    }

    @HttpMethod(HttpVerb.GET, "/api/v1/author")
    fun getAuthors(): RestResult {
        val res = GSON.toJson(dao.getAuthors())
        return RestResult.json(res)
    }
}