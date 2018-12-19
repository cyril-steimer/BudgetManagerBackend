package ch.cyril.budget.manager.backend.service.filebased.budget

import ch.cyril.budget.manager.backend.model.Budget
import ch.cyril.budget.manager.backend.model.Category
import ch.cyril.budget.manager.backend.service.budget.BudgetDao
import ch.cyril.budget.manager.backend.util.SubList
import java.nio.file.Path

class FilebasedBudgetDao(val file: Path) : BudgetDao {

    private val parser = BudgetParser()

    override fun getBudgets(): SubList<Budget> {
        return SubList.of(parser.load(file))
    }

    override fun getOneBudget(category: Category): Budget? {
        return getAllBudgets()
                .find { b -> b.category == category }
    }

    override fun addBudget(budget: Budget) {
        val budgets = ArrayList(getAllBudgets())
        budgets.removeIf { b -> b.category == budget.category }
        budgets.add(budget)
        parser.store(file, budgets)
    }

    override fun updateBudget(budget: Budget) {
        addBudget(budget)
    }

    override fun deleteBudget(budget: Budget) {
        val budgets = ArrayList(getAllBudgets())
        budgets.removeIf { b -> b.category == budget.category }
        parser.store(file, budgets)
    }

    override fun getCategories(): SubList<Category> {
        val res = getBudgets().values
                .map { b -> b.category }
        return SubList.of(res)
    }

    private fun getAllBudgets(): List<Budget> {
        return parser.load(file)
    }
}