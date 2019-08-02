package ch.cyril.budget.manager.backend.service.filebased.expense

import ch.cyril.budget.manager.backend.model.*
import ch.cyril.budget.manager.backend.service.filebased.JsonBasedFileParser
import com.google.gson.Gson

class ExpenseParser() : JsonBasedFileParser<Expense>(Expense::class.java, Gson())