package ch.cyril.budget.manager.backend.service.mongo.budget

import ch.cyril.budget.manager.backend.model.Amount
import ch.cyril.budget.manager.backend.service.mongo.*
import ch.cyril.budget.manager.backend.model.Budget
import ch.cyril.budget.manager.backend.model.BudgetPeriod
import ch.cyril.budget.manager.backend.model.Category
import ch.cyril.budget.manager.backend.util.Identifiable
import org.bson.Document
import org.bson.types.Decimal128
import java.math.BigDecimal

class MongoBudgetSerialization {

    fun serialize(budget: Budget): Document {
        // TODO Can this be automated?
        return Document()
                .append(KEY_CATEGORY, budget.category.name)
                .append(KEY_AMOUNT, budget.amount.amount)
                .append(KEY_PERIOD, budget.period.identifier)
    }

    fun deserialize(doc: Document): Budget {
        val category = Category(doc.getString(KEY_CATEGORY))
        val amount = Amount(doc.get(KEY_AMOUNT, Decimal128::class.java).bigDecimalValue())
        val period = Identifiable.byIdentifier<BudgetPeriod>(doc.getString(KEY_PERIOD))
        return Budget(category, amount, period)
    }
}