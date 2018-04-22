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
                t.date.timestamp,
                t.method.name)
                .joinToString(",")
    }

    override fun fromLine(line: String): Expense {
        val split = line.split(",")
        val id = Id(split[0].toInt())
        val name = Name(split[1])
        val amount = Amount(split[2].toBigDecimal())
        val category = Category(split[3])
        val date = parseTimestampBackwardsCompatible(split[4])
        var method = PaymentMethod("")
        if (split.size >= 6) {
            method = PaymentMethod(split[5]);
        }
        return Expense(id, name, amount, category, date, method)
    }

    private fun parseTimestampBackwardsCompatible(date: String): Timestamp {
        try {
            return Timestamp(date.toLong())
        } catch (e: NumberFormatException) {
            val res = LocalDate.parse(date)
            return Timestamp(res.toEpochDay() * (1000 * 60 * 60 * 24))
        }
    }
}