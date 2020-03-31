package ch.cyril.budget.manager.backend.service.filebased

import ch.cyril.budget.manager.backend.service.ServiceFactory
import ch.cyril.budget.manager.backend.service.budget.BudgetDao
import ch.cyril.budget.manager.backend.service.expense.ActualExpenseDao
import ch.cyril.budget.manager.backend.service.expense.ExpenseTemplateDao
import ch.cyril.budget.manager.backend.service.expense.ScheduledExpenseDao
import ch.cyril.budget.manager.backend.service.filebased.budget.FilebasedBudgetDao
import ch.cyril.budget.manager.backend.service.filebased.expense.FilebasedActualExpenseDao
import ch.cyril.budget.manager.backend.service.filebased.expense.FilebasedExpenseTemplateDao
import ch.cyril.budget.manager.backend.service.filebased.expense.FilebasedScheduledExpenseDao
import java.nio.file.Path

class FilebasedServiceFactory(
        private val expenseFile: Path,
        private val templateFile: Path,
        private val scheduledExpenseFile: Path,
        private val budgetFile: Path) : ServiceFactory {

    override fun createExpenseDao(): ActualExpenseDao {
        return FilebasedActualExpenseDao(expenseFile)
    }

    override fun createTemplateDao(): ExpenseTemplateDao {
        return FilebasedExpenseTemplateDao(templateFile)
    }

    override fun createScheduledExpenseDao(): ScheduledExpenseDao {
        return FilebasedScheduledExpenseDao(scheduledExpenseFile)
    }

    override fun createBudgetDao(): BudgetDao {
        return FilebasedBudgetDao(budgetFile)
    }
}