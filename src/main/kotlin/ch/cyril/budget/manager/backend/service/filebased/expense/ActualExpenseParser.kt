package ch.cyril.budget.manager.backend.service.filebased.expense

import ch.cyril.budget.manager.backend.model.ActualExpense
import ch.cyril.budget.manager.backend.model.ExpenseTemplate
import ch.cyril.budget.manager.backend.model.ScheduledExpense
import ch.cyril.budget.manager.backend.service.filebased.JsonBasedFileParser

class ActualExpenseParser() : JsonBasedFileParser<ActualExpense>(ActualExpense::class.java)

class ExpenseTemplateParser() : JsonBasedFileParser<ExpenseTemplate>(ExpenseTemplate::class.java)

class ScheduledExpenseParser(): JsonBasedFileParser<ScheduledExpense>(ScheduledExpense::class.java)