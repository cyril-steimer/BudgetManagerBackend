package ch.cyril.budget.manager.backend.service.filebased.expense

import ch.cyril.budget.manager.backend.model.*
import ch.cyril.budget.manager.backend.service.filebased.LineBasedFileParser
import java.time.LocalDate

class ExpenseParser() : LineBasedFileParser<Expense>() {

    override fun toLine(t: Expense): String {
        val list = listOf(
                t.id.id,
                t.name.name,
                t.amount.amount,
                t.category.name,
                t.date.timestamp,
                t.method.name)
        val res = ArrayList(list)
        res.addAll(t.tags.map { t -> t.name })
        return res.joinToString(",")
    }

    override fun fromLine(line: String): Expense {
        val split = line.split(",")
        val id = Id(split[0].toInt())
        val name = Name(split[1])
        val amount = Amount(split[2].toBigDecimal())
        val category = Category(split[3])
        val date = parseTimestampBackwardsCompatible(split[4])
        val method = PaymentMethod(split[5])
        val tags = split.subList(6, split.size).map { t -> Tag(t) }.toSet()
        return Expense(id, name, amount, category, date, method, tags)
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