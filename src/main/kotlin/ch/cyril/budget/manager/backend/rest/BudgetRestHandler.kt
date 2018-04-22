package ch.cyril.budget.manager.backend.rest

import ch.cyril.budget.manager.backend.model.Budget
import ch.cyril.budget.manager.backend.rest.lib.Body
import ch.cyril.budget.manager.backend.rest.lib.HttpMethod
import ch.cyril.budget.manager.backend.rest.lib.HttpVerb
import ch.cyril.budget.manager.backend.rest.lib.RestResult
import ch.cyril.budget.manager.backend.service.budget.BudgetDao

class BudgetRestHandler(private val budgetDao: BudgetDao) {

    @HttpMethod(HttpVerb.GET, "/api/v1/budget")
    fun getBudgets(): RestResult {
        val res = budgetDao.getBudgets()
        return RestResult.json(GSON.toJson(res))
    }

    @HttpMethod(HttpVerb.POST, "/api/v1/budget")
    fun addBudget(@Body budget: Budget){
        budgetDao.addBudget(budget)
    }

    @HttpMethod(HttpVerb.GET, "/api/v1/category")
    fun getCategories(): RestResult {
        val res = budgetDao.getCategories()
        return RestResult.json(GSON.toJson(res))
    }
}