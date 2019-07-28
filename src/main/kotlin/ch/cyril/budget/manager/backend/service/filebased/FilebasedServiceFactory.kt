package ch.cyril.budget.manager.backend.service.filebased

import ch.cyril.budget.manager.backend.service.ServiceFactory
import ch.cyril.budget.manager.backend.service.budget.BudgetDao
import ch.cyril.budget.manager.backend.service.expense.ExpenseDao
import ch.cyril.budget.manager.backend.service.filebased.budget.FilebasedBudgetDao
import ch.cyril.budget.manager.backend.service.filebased.expense.FilebasedExpenseDao
import java.nio.file.Path

class FilebasedServiceFactory(
        private val expenseFile: Path,
        private val templateFile: Path,
        private val budgetFile: Path) : ServiceFactory {

    override fun createExpenseDao(): ExpenseDao {
        return FilebasedExpenseDao(expenseFile)
    }

    override fun createTemplateDao(): ExpenseDao {
        return FilebasedExpenseDao(templateFile)
    }

    override fun createBudgetDao(): BudgetDao {
        return FilebasedBudgetDao(budgetFile)
    }
}