package ch.cyril.budget.manager.backend.service.filebased.budget

import ch.cyril.budget.manager.backend.model.Amount
import ch.cyril.budget.manager.backend.model.Budget
import ch.cyril.budget.manager.backend.model.Category
import ch.cyril.budget.manager.backend.service.filebased.LineBasedFileParser

class BudgetParser() : LineBasedFileParser<Budget>() {

    override fun toLine(t: Budget): String {
        return listOf(
                t.category.name,
                t.amount.amount)
                .joinToString(",")
    }

    override fun fromLine(line: String): Budget {
        val split = line.split(",")
        val category = Category(split[0])
        val amount = Amount(split[1].toBigDecimal())
        return Budget(category, amount)
    }
}