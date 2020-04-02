package ch.cyril.budget.manager.backend.service.filebased.view

import ch.cyril.budget.manager.backend.model.BudgetView
import ch.cyril.budget.manager.backend.model.Id
import ch.cyril.budget.manager.backend.service.budget.BudgetDao
import ch.cyril.budget.manager.backend.service.filebased.FileContentCache
import ch.cyril.budget.manager.backend.service.filebased.FilebasedDao
import ch.cyril.budget.manager.backend.service.filebased.JsonBasedFileParser
import ch.cyril.budget.manager.backend.service.filebased.expense.getBudgetByIdGson
import ch.cyril.budget.manager.backend.service.view.ViewDao
import ch.cyril.budget.manager.backend.util.SubList
import java.nio.file.Path

class BudgetViewParser(budgetDao: BudgetDao) : JsonBasedFileParser<BudgetView>(BudgetView::class.java, getBudgetByIdGson(budgetDao))

class FilebasedViewDao(budgetViewPath: Path, budgetDao: BudgetDao) : ViewDao, FilebasedDao {

    private val budgetViewCache: FileContentCache<BudgetView, Id>

    init {
        budgetViewCache = FileContentCache(budgetViewPath, BudgetViewParser(budgetDao)) { view -> view.id }
    }

    override fun getBudgetViews(): SubList<BudgetView> {
        return SubList.of(budgetViewCache.getAll().sortedBy { v -> v.order.order })
    }

    override fun getBudgetView(id: Id): BudgetView? {
        return budgetViewCache.getById(id)
    }

    override fun reloadFromFile() {
        return budgetViewCache.reloadFromFile()
    }
}