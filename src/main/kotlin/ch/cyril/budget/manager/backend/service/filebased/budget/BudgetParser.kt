package ch.cyril.budget.manager.backend.service.filebased.budget

import ch.cyril.budget.manager.backend.model.*
import ch.cyril.budget.manager.backend.service.filebased.LineBasedFileParser
import ch.cyril.budget.manager.backend.util.Identifiable

class BudgetParser() : LineBasedFileParser<Budget>() {

    override fun toLine(t: Budget): String {
        val list = ArrayList<Any>()
        list.add(t.category.name)
        for (a in t.amounts) {
            list.addAll(listOf(
                    a.period.identifier,
                    a.amount.amount,
                    a.from.month,
                    a.from.year,
                    a.to.month,
                    a.to.year))
        }
        return list.joinToString(",")
    }

    override fun fromLine(line: String): Budget {
        val split = line.split(",")
        val category = Category(split[0])
        if (split.size == 3) {
            val amount = Amount(split[1].toBigDecimal())
            val period = Identifiable.byIdentifier<BudgetPeriod>(split[2])
            return Budget(category, listOf(BudgetAmount(amount, period, MonthYear(1, 0), MonthYear(1, 9999))))
        }
        val amounts = ArrayList<BudgetAmount>()
        for (i in 1 until split.size step 6) {
            amounts.add(parseBudgetAmount(split, i))
        }
        return Budget(category, amounts)
    }

    private fun parseBudgetAmount (split: List<String>, index: Int): BudgetAmount {
        val period = Identifiable.byIdentifier<BudgetPeriod>(split[index])
        val amount = Amount(split[index + 1].toBigDecimal())
        val from = MonthYear(split[index + 2].toInt(), split[index + 3].toInt())
        val to = MonthYear(split[index + 4].toInt(), split[index + 4].toInt())
        return BudgetAmount(amount, period, from, to)
    }
}