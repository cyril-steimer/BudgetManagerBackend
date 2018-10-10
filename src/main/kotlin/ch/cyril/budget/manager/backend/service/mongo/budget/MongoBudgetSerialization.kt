package ch.cyril.budget.manager.backend.service.mongo.budget

import ch.cyril.budget.manager.backend.model.*
import ch.cyril.budget.manager.backend.service.mongo.*
import ch.cyril.budget.manager.backend.util.Identifiable
import org.bson.Document
import org.bson.types.Decimal128

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
                .append(KEY_FROM_MONTH, amount.from.month)
                .append(KEY_FROM_YEAR, amount.from.year)
                .append(KEY_TO_MONTH, amount.to.month)
                .append(KEY_TO_YEAR, amount.to.year)
    }

    private fun deserializeBudgetAmount(doc: Document): BudgetAmount {
        val amount = Amount(doc.get(KEY_AMOUNT, Decimal128::class.java).bigDecimalValue())
        val period = Identifiable.byIdentifier<BudgetPeriod>(doc.getString(KEY_PERIOD))
        val from = MonthYear(doc.getInteger(KEY_FROM_MONTH), doc.getInteger(KEY_FROM_YEAR))
        val to = MonthYear(doc.getInteger(KEY_TO_MONTH), doc.getInteger(KEY_TO_YEAR))
        return BudgetAmount(amount, period, from, to)
    }
}