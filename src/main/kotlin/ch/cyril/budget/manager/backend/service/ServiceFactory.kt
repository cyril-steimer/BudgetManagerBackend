package ch.cyril.budget.manager.backend.service

import ch.cyril.budget.manager.backend.service.budget.BudgetDao
import ch.cyril.budget.manager.backend.service.expense.ActualExpenseDao
import ch.cyril.budget.manager.backend.service.expense.ExpenseDao
import ch.cyril.budget.manager.backend.service.expense.ExpenseTemplateDao

interface ServiceFactory {

    fun createExpenseDao(): ActualExpenseDao

    fun createTemplateDao(): ExpenseTemplateDao

    fun createBudgetDao(): BudgetDao
}