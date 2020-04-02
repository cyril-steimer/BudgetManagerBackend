package ch.cyril.budget.manager.backend.service.filebased.budget

import ch.cyril.budget.manager.backend.model.Budget
import ch.cyril.budget.manager.backend.model.BudgetWithoutId
import ch.cyril.budget.manager.backend.model.Category
import ch.cyril.budget.manager.backend.model.Id
import ch.cyril.budget.manager.backend.rest.GSON
import ch.cyril.budget.manager.backend.service.budget.BudgetDao
import ch.cyril.budget.manager.backend.service.filebased.FileContentCache
import ch.cyril.budget.manager.backend.service.filebased.FilebasedDao
import ch.cyril.budget.manager.backend.service.filebased.JsonBasedFileParser
import ch.cyril.budget.manager.backend.service.filebased.expense.getNewId
import ch.cyril.budget.manager.backend.util.SubList
import java.nio.file.Path

class BudgetParser() : JsonBasedFileParser<Budget>(Budget::class.java, GSON)

class FilebasedBudgetDao(file: Path) : BudgetDao, FilebasedDao {

    var daosToReloadAfterUpdate: List<FilebasedDao> = emptyList()

    private val contentCache: FileContentCache<Budget, Id>

    init {
        contentCache = FileContentCache(file, BudgetParser()) { b -> b.id }
    }

    override fun reloadFromFile() {
        contentCache.reloadFromFile()
    }

    override fun getBudgets(): SubList<Budget> {
        return SubList.of(contentCache.getAll())
    }

    override fun getOneBudget(id: Id): Budget? {
        return contentCache.getById(id)
    }

    override fun getOneBudget(category: Category): Budget? {
        // TODO Optimize?
        return contentCache.getAll()
                .find { b -> b.category == category }
    }

    override fun addBudget(budget: BudgetWithoutId): Budget {
        val newId = getNewId(contentCache.getAll().map { b -> b.id })
        return contentCache.add(budget.withId(newId))
    }

    override fun updateBudget(budget: Budget) {
        val existing = getOneBudget(budget.category)
        if (existing != null && existing.id != budget.id) {
            throw IllegalArgumentException("There is already another budget with category '${budget.category.name}'")
        }
        contentCache.update(budget)
        for (expenseDao in daosToReloadAfterUpdate) {
            // If we changed the category of the budget, all expenses must be reloaded
            expenseDao.reloadFromFile()
        }
    }

    override fun deleteBudget(id: Id) {
        contentCache.delete(id)
    }
}