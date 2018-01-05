package ch.cyril.budget.manager.backend.service.budget

import ch.cyril.budget.manager.backend.model.Budget
import ch.cyril.budget.manager.backend.util.SubList

interface BudgetDao {

    fun getBudgets(): SubList<Budget>

    fun addBudget(budget: Budget)

    fun updateBudget(budget: Budget)

    fun deleteBudget(budget: Budget)
}