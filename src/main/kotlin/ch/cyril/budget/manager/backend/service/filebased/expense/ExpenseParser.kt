package ch.cyril.budget.manager.backend.service.filebased.expense

import ch.cyril.budget.manager.backend.model.*
import ch.cyril.budget.manager.backend.service.filebased.LineBasedFileParser

class ExpenseParser() : LineBasedFileParser<Expense>() {

    companion object {
        private const val VERSION_WITH_AUTHOR = "___VERSION=1.0___"
    }

    override fun toLine(t: Expense): String {
        val list = mutableListOf(
                VERSION_WITH_AUTHOR,
                t.id.id,
                t.name.name,
                t.amount.amount,
                t.category.name,
                t.date.timestamp,
                t.method.name,
                t.author.name)
        list.addAll(t.tags.map { tag -> tag.name })
        return list.joinToString(",")
    }

    override fun fromLine(line: String): Expense {
        val split = line.split(",")
        if (split[0] == VERSION_WITH_AUTHOR) {
            return parse(split, true, 1)
        }
        return parse(split, false, 0)
    }

    private fun parse(line: List<String>, withAuthor: Boolean, startIndex: Int): Expense {
        var index = startIndex
        val id = Id(line[index++])
        val name = Name(line[index++])
        val amount = Amount(line[index++].toBigDecimal())
        val category = Category(line[index++])
        val date = Timestamp(line[index++].toLong())
        val method = PaymentMethod(line[index++])
        var author = Author("")
        if (withAuthor) {
            author = Author(line[index++])
        }
        val tags = line.subList(index, line.size).map { t -> Tag(t) }.toSet()
        return Expense(id, name, amount, category, date, method, author, tags)
    }
}