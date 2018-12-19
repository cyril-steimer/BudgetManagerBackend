package ch.cyril.budget.manager.backend.service.budget

import ch.cyril.budget.manager.backend.model.*
import ch.cyril.budget.manager.backend.util.SubList
import java.math.BigDecimal
import java.math.RoundingMode

interface BudgetDao {

    fun getCategories(): SubList<Category>

    fun getBudgets(period: MonthYearPeriod): SubList<BudgetInPeriod> {
        // TODO Can this be optimized for some DAOs?
        val all = getBudgets().values
        val res = getBudgetsInPeriod(all, period)
        return SubList.of(res)
    }

    fun getOneBudget(category: Category): Budget?

    fun getBudgets(): SubList<Budget>

    fun addBudget(budget: Budget)

    fun updateBudget(budget: Budget)

    fun deleteBudget(category: Category)

    fun getBudgetsInPeriod(budgets: List<Budget>, period: MonthYearPeriod): List<BudgetInPeriod> {
        return budgets.map { b -> getBudget(b, period.from, period.to) }
                .filterNotNull()
    }

    private fun getBudget(budget: Budget, from: MonthYear, to: MonthYear): BudgetInPeriod? {
        val amount = budget.amounts
                .map { a -> calculateOverlap(a, from, to) }
                .filterNotNull()
                .fold(BigDecimal.ZERO) { acc, amount -> acc.add(amount.amount) }

        return if (amount == BigDecimal.ZERO) null else BudgetInPeriod(budget.category, Amount(amount))
    }

    private fun calculateOverlap(amount: BudgetAmount, from: MonthYear, to: MonthYear): Amount? {
        val start = Math.max(amount.from.toEpochMonth(), from.toEpochMonth())
        val end = Math.min(amount.to.toEpochMonth(), to.toEpochMonth())
        if (start > end) {
            return null
        }
        val months = (end - start) + 1
        val divisor = if (amount.period == BudgetPeriod.MONTHLY) BigDecimal.ONE else BigDecimal("12")
        val res = amount.amount.amount
                .multiply(BigDecimal.valueOf(months.toLong()))
                .divide(divisor, 2, RoundingMode.HALF_UP)
        return Amount(res)
    }
}