package ch.cyril.budget.manager.backend.service.mongo.budget

import ch.cyril.budget.manager.backend.model.*
import ch.cyril.budget.manager.backend.service.mongo.*
import ch.cyril.budget.manager.backend.util.Identifiable
import org.bson.Document
import org.bson.types.Decimal128
import java.time.Instant
import java.time.LocalDate

class MongoBudgetSerialization {

    fun serialize(budget: Budget): Document {
        // TODO Can this be automated?
        return Document(KEY_ID, budget.category.name)
                .append(KEY_AMOUNT, budget.amounts.map { a -> serializeBudgetAmount(a) })
    }

    fun deserialize(doc: Document): Budget {
        val category = Category(doc.getString(KEY_ID))
        val amounts = doc.get(KEY_AMOUNT, List::class.java)
                .map { it -> deserializeBudgetAmount(it as Document) }
        return Budget(category, amounts.toList())
    }

    private fun serializeBudgetAmount(amount: BudgetAmount): Document {
        return Document()
                .append(KEY_AMOUNT, amount.amount.amount)
                .append(KEY_PERIOD, amount.period.identifier)
                .append(KEY_FROM, LocalDate.of(amount.from.year, amount.from.month, 1).toEpochDay())
                .append(KEY_TO, LocalDate.of(amount.to.year, amount.to.month, 1).toEpochDay())
    }

    private fun deserializeBudgetAmount(doc: Document): BudgetAmount {
        val amount = Amount(doc.get(KEY_AMOUNT, Decimal128::class.java).bigDecimalValue())
        val period = Identifiable.byIdentifier<BudgetPeriod>(doc.getString(KEY_PERIOD))
        val from = LocalDate.ofEpochDay(doc.getLong(KEY_FROM))
        val to = LocalDate.ofEpochDay(doc.getLong(KEY_TO))
        return BudgetAmount(amount, period, MonthYear(from.monthValue, from.year), MonthYear(to.monthValue, to.year))
    }
}