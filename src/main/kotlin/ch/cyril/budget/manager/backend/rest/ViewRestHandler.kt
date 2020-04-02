package ch.cyril.budget.manager.backend.rest

import ch.cyril.budget.manager.backend.model.Id
import ch.cyril.budget.manager.backend.rest.lib.HttpMethod
import ch.cyril.budget.manager.backend.rest.lib.HttpVerb
import ch.cyril.budget.manager.backend.rest.lib.PathParam
import ch.cyril.budget.manager.backend.rest.lib.RestResult
import ch.cyril.budget.manager.backend.service.view.ViewDao

class ViewRestHandler(private val viewDao: ViewDao) {

    @HttpMethod(HttpVerb.GET, "/api/v1/view/budget")
    fun getBudgetViews(): RestResult {
        val res = viewDao.getBudgetViews()
        return RestResult.json(GSON.toJson(res))
    }

    @HttpMethod(HttpVerb.GET, "/api/v1/view/budget/id/:id")
    fun getBudgetViewById(@PathParam("id") id: String): RestResult {
        val res = viewDao.getBudgetView(Id(id)) ?: throw IllegalArgumentException("No budget view with id ${id}")
        return RestResult.json(GSON.toJson(res))
    }
}