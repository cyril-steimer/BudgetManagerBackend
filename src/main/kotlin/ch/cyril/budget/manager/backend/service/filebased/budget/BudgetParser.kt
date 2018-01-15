package ch.cyril.budget.manager.backend.service.filebased.budget

import ch.cyril.budget.manager.backend.model.Amount
import ch.cyril.budget.manager.backend.model.Budget
import ch.cyril.budget.manager.backend.model.BudgetPeriod
import ch.cyril.budget.manager.backend.model.Category
import ch.cyril.budget.manager.backend.service.filebased.LineBasedFileParser
import ch.cyril.budget.manager.backend.util.Identifiable

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
        val period = Identifiable.byIdentifier<BudgetPeriod>(split[2])
        return Budget(category, amount, period)
    }
}