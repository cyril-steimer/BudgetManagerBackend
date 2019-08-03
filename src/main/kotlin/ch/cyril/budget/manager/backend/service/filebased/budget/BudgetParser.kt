package ch.cyril.budget.manager.backend.service.filebased.budget

import ch.cyril.budget.manager.backend.model.*
import ch.cyril.budget.manager.backend.service.filebased.JsonBasedFileParser

class BudgetParser() : JsonBasedFileParser<Budget>(Budget::class.java)