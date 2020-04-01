package ch.cyril.budget.manager.backend.service.filebased

import ch.cyril.budget.manager.backend.service.ServiceFactory
import ch.cyril.budget.manager.backend.service.filebased.budget.FilebasedBudgetDao
import ch.cyril.budget.manager.backend.service.filebased.expense.FilebasedActualExpenseDao
import ch.cyril.budget.manager.backend.service.filebased.expense.FilebasedExpenseTemplateDao
import ch.cyril.budget.manager.backend.service.filebased.expense.FilebasedScheduledExpenseDao
import java.nio.file.Path

class FilebasedServiceFactory(expenseFile: Path, templateFile: Path, scheduledExpenseFile: Path, budgetFile: Path) : ServiceFactory {

    override val budgetDao: FilebasedBudgetDao
    override val expenseDao: FilebasedActualExpenseDao
    override val templateDao: FilebasedExpenseTemplateDao
    override val scheduledExpenseDao: FilebasedScheduledExpenseDao

    init {
        budgetDao = FilebasedBudgetDao(budgetFile)
        expenseDao = FilebasedActualExpenseDao(expenseFile, budgetDao)
        templateDao = FilebasedExpenseTemplateDao(templateFile, budgetDao)
        scheduledExpenseDao = FilebasedScheduledExpenseDao(scheduledExpenseFile, budgetDao)
        budgetDao.expenseDaos = listOf(expenseDao, templateDao, scheduledExpenseDao)
    }
}