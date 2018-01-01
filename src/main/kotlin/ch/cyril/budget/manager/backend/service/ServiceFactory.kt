package ch.cyril.budget.manager.backend.service

import ch.cyril.budget.manager.backend.service.expense.ExpenseDao

interface ServiceFactory {

    fun createExpenseDao(): ExpenseDao
}