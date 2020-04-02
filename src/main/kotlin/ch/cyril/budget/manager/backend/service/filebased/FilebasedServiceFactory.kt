package ch.cyril.budget.manager.backend.service.filebased

import ch.cyril.budget.manager.backend.service.ServiceFactory
import ch.cyril.budget.manager.backend.service.filebased.budget.FilebasedBudgetDao
import ch.cyril.budget.manager.backend.service.filebased.expense.FilebasedActualExpenseDao
import ch.cyril.budget.manager.backend.service.filebased.expense.FilebasedExpenseTemplateDao
import ch.cyril.budget.manager.backend.service.filebased.expense.FilebasedScheduledExpenseDao
import ch.cyril.budget.manager.backend.service.filebased.view.FilebasedViewDao
import java.nio.file.Paths

interface FilebasedDao {
    fun reloadFromFile()
}

class FilebasedServiceFactory(
        expenseFile: String,
        templateFile: String,
        scheduledExpenseFile: String,
        budgetFile: String,
        budgetViewFile: String) : ServiceFactory {

    override val budgetDao: FilebasedBudgetDao
    override val expenseDao: FilebasedActualExpenseDao
    override val templateDao: FilebasedExpenseTemplateDao
    override val scheduledExpenseDao: FilebasedScheduledExpenseDao
    override val viewDao: FilebasedViewDao

    init {
        budgetDao = FilebasedBudgetDao(Paths.get(budgetFile))
        expenseDao = FilebasedActualExpenseDao(Paths.get(expenseFile), budgetDao)
        templateDao = FilebasedExpenseTemplateDao(Paths.get(templateFile), budgetDao)
        scheduledExpenseDao = FilebasedScheduledExpenseDao(Paths.get(scheduledExpenseFile), budgetDao)
        viewDao = FilebasedViewDao(Paths.get(budgetViewFile), budgetDao)
        budgetDao.daosToReloadAfterUpdate = listOf(expenseDao, templateDao, scheduledExpenseDao, viewDao)
    }
}