package ch.cyril.budget.manager.backend.service.filebased.expense

import ch.cyril.budget.manager.backend.model.*
import ch.cyril.budget.manager.backend.service.filebased.JsonBasedFileParser

class ActualExpenseParser() : JsonBasedFileParser<ActualExpense>(ActualExpense::class.java)

class ExpenseTemplateParser() : JsonBasedFileParser<ExpenseTemplate>(ExpenseTemplate::class.java)