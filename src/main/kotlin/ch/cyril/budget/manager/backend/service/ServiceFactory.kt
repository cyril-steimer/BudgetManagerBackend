package ch.cyril.budget.manager.backend.service

import ch.cyril.budget.manager.backend.service.budget.BudgetDao
import ch.cyril.budget.manager.backend.service.expense.ActualExpenseDao
import ch.cyril.budget.manager.backend.service.expense.ExpenseTemplateDao
import ch.cyril.budget.manager.backend.service.expense.ScheduledExpenseDao

interface ServiceFactory {
    val expenseDao: ActualExpenseDao
    val templateDao: ExpenseTemplateDao
    val scheduledExpenseDao: ScheduledExpenseDao
    val budgetDao: BudgetDao
}