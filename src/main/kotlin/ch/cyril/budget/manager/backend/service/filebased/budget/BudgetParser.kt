package ch.cyril.budget.manager.backend.service.filebased.budget

import ch.cyril.budget.manager.backend.model.Budget
import ch.cyril.budget.manager.backend.rest.GSON
import ch.cyril.budget.manager.backend.service.filebased.JsonBasedFileParser

class BudgetParser() : JsonBasedFileParser<Budget>(Budget::class.java, GSON)