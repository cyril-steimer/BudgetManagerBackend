package ch.cyril.budget.manager.backend.service.filebased.expense

import ch.cyril.budget.manager.backend.model.*
import ch.cyril.budget.manager.backend.service.filebased.LineBasedFileParser
import java.time.LocalDate

class ExpenseParser() : LineBasedFileParser<Expense>() {

    override fun toLine(t: Expense): String {
        return listOf(
                t.id.id,
                t.name.name,
                t.amount.amount,
                t.category.name,
                t.date)
                .joinToString(",")
    }

    override fun fromLine(line: String): Expense {
        val split = line.split(",")
        val id = Id(split[0].toInt())
        val name = Name(split[1])
        val amount = Amount(split[2].toBigDecimal())
        val category = Category(split[3])
        val date = LocalDate.parse(split[4])
        return Expense(id, name, amount, category, date)
    }
}