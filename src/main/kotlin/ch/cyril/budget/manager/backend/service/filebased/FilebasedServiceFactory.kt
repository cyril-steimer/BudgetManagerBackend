package ch.cyril.budget.manager.backend.service.filebased

import ch.cyril.budget.manager.backend.service.ServiceFactory
import ch.cyril.budget.manager.backend.service.expense.ExpenseDao
import ch.cyril.budget.manager.backend.service.filebased.expense.FilebasedExpenseDao
import java.nio.file.Path

class FilebasedServiceFactory(val expenseFile: Path) : ServiceFactory {

    override fun createExpenseDao(): ExpenseDao {
        return FilebasedExpenseDao(expenseFile)
    }
}