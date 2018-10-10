package ch.cyril.budget.manager.backend.service.budget

import ch.cyril.budget.manager.backend.model.*
import ch.cyril.budget.manager.backend.util.SubList
import java.math.BigDecimal
import java.time.LocalDate

interface BudgetDao {

    fun getCategories(): SubList<Category>

    fun getBudgets(period: MonthYearPeriod): SubList<BudgetInPeriod> {
        // TODO Can this be optimized for some DAOs?
        val all = getBudgets().values
        val res = getBudgetsInPeriod(all, period)
        return SubList.of(res)
    }

    fun getBudgets(): SubList<Budget>

    fun addBudget(budget: Budget)

    fun updateBudget(budget: Budget)

    fun deleteBudget(budget: Budget)

    fun getBudgetsInPeriod(budgets: List<Budget>, period: MonthYearPeriod): List<BudgetInPeriod> {
        return budgets.map { b -> getBudget(b, period.from, period.to) }
                .filterNotNull()
    }

    private fun getBudget(budget: Budget, from: MonthYear, to: MonthYear): BudgetInPeriod? {
        val amount = budget.amounts
                .map { a -> calculateOverlap(a, from, to) }
                .find { a  -> a != null }
        return if (amount == null) null else BudgetInPeriod(budget.category, amount)
    }

    private fun calculateOverlap(amount: BudgetAmount, from: MonthYear, to: MonthYear): Amount? {
        val start = Math.max(amount.from.toEpochMonth(), from.toEpochMonth())
        val end = Math.min(amount.to.toEpochMonth(), to.toEpochMonth())
        if (start > end) {
            return null
        }
        val divisor = if (amount.period == BudgetPeriod.MONTHLY) 1.0 else 12.0
        val multiplier = ((end - start) + 1) / divisor
        val res = amount.amount.amount.multiply(BigDecimal.valueOf(multiplier))
        return Amount(res)
    }
}