package ch.cyril.budget.manager.backend.service.filebased.budget

import ch.cyril.budget.manager.backend.model.Budget
import ch.cyril.budget.manager.backend.model.Category
import ch.cyril.budget.manager.backend.service.budget.BudgetDao
import ch.cyril.budget.manager.backend.service.filebased.FileContentCache
import ch.cyril.budget.manager.backend.util.SubList
import java.nio.file.Path

class FilebasedBudgetDao(file: Path) : BudgetDao {

    private val contentCache: FileContentCache<Budget, Category>

    init {
        contentCache = FileContentCache(file, BudgetParser()) { b -> b.category }
    }

    override fun getBudgets(): SubList<Budget> {
        return SubList.of(contentCache.getAll())
    }

    override fun getOneBudget(category: Category): Budget? {
        return contentCache.getById(category)
    }

    override fun addBudget(budget: Budget) {
        contentCache.add(budget)
    }

    override fun updateBudget(budget: Budget) {
        contentCache.update(budget)
    }

    override fun deleteBudget(category: Category) {
        contentCache.delete(category)
    }

    override fun getCategories(): SubList<Category> {
        val res = contentCache.getAll()
                .map { b -> b.category }
        return SubList.of(res)
    }
}