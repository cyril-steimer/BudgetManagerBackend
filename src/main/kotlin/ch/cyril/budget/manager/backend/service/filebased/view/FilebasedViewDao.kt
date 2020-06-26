package ch.cyril.budget.manager.backend.service.filebased.view

import ch.cyril.budget.manager.backend.model.Id
import ch.cyril.budget.manager.backend.model.View
import ch.cyril.budget.manager.backend.model.ViewType
import ch.cyril.budget.manager.backend.service.budget.BudgetDao
import ch.cyril.budget.manager.backend.service.filebased.FileContentCache
import ch.cyril.budget.manager.backend.service.filebased.FilebasedDao
import ch.cyril.budget.manager.backend.service.filebased.JsonBasedFileParser
import ch.cyril.budget.manager.backend.service.filebased.expense.getBudgetByIdGson
import ch.cyril.budget.manager.backend.service.view.ViewDao
import ch.cyril.budget.manager.backend.util.SubList
import java.nio.file.Path

class BudgetViewParser(budgetDao: BudgetDao) : JsonBasedFileParser<View>(View::class.java, getBudgetByIdGson(budgetDao))

class FilebasedViewDao(viewPath: Path, budgetDao: BudgetDao) : ViewDao, FilebasedDao {

    private val viewCache: FileContentCache<View, Id>

    init {
        viewCache = FileContentCache(viewPath, BudgetViewParser(budgetDao)) { view -> view.id }
    }

    override fun getViews(type: ViewType): SubList<View> {
        return SubList.of(viewCache.getAll()
                .filter { type == it.type }
                .sortedBy { it.order.order })
    }

    override fun getView(id: Id): View? {
        return viewCache.getById(id)
    }

    override fun reloadFromFile() {
        return viewCache.reloadFromFile()
    }
}