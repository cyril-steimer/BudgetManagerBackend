package ch.cyril.budget.manager.backend.rest

import ch.cyril.budget.manager.backend.model.Budget
import ch.cyril.budget.manager.backend.model.Category
import ch.cyril.budget.manager.backend.model.MonthYearPeriod
import ch.cyril.budget.manager.backend.rest.lib.*
import ch.cyril.budget.manager.backend.service.budget.BudgetDao
import java.lang.IllegalArgumentException

class BudgetRestHandler(private val budgetDao: BudgetDao) {

    @HttpMethod(HttpVerb.GET, "/api/v1/budget")
    fun getBudgets(): RestResult {
        val res = budgetDao.getBudgets()
        return RestResult.json(GSON.toJson(res))
    }

    @HttpMethod(HttpVerb.POST, "/api/v1/budget/period")
    fun getBudgets(@Body period: MonthYearPeriod): RestResult {
        val res = budgetDao.getBudgets(period)
        return RestResult.json(GSON.toJson(res))
    }

    @HttpMethod(HttpVerb.GET, "/api/v1/budget/category/:category")
    fun getBudget(@PathParam("category") category: String): RestResult {
        val res = budgetDao.getOneBudget(Category(category))
        val nonNull = res ?: throw IllegalArgumentException("No budget with category $category")
        return RestResult.json(GSON.toJson(nonNull))
    }

    @HttpMethod(HttpVerb.POST, "/api/v1/budget")
    fun addBudget(@Body budget: Budget) {
        budgetDao.addBudget(budget)
    }

    @HttpMethod(HttpVerb.PUT, "/api/v1/budget")
    fun updateBudget(@Body budget: Budget) {
        budgetDao.updateBudget(budget)
    }

    @HttpMethod(HttpVerb.DELETE, "/api/v1/budget")
    fun deleteBudget(@QueryParam("category") category: String) {
        val budget = budgetDao.getOneBudget(Category(category))
        if (budget != null) {
            budgetDao.deleteBudget(budget)
        }
    }

    @HttpMethod(HttpVerb.GET, "/api/v1/category")
    fun getCategories(): RestResult {
        val res = budgetDao.getCategories()
        return RestResult.json(GSON.toJson(res))
    }
}