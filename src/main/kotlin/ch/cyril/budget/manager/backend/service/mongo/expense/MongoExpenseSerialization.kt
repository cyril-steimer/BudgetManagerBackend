package ch.cyril.budget.manager.backend.service.mongo.expense

import ch.cyril.budget.manager.backend.model.*
import org.bson.Document
import ch.cyril.budget.manager.backend.service.mongo.*
import org.bson.types.Decimal128

class MongoExpenseSerialization {

    fun serialize(expense: Expense): Document {
        // TODO Can this be automated?
        return Document()
                .append(KEY_TAGS, expense.tags.map { tag -> tag.name })
                .append(KEY_AMOUNT, expense.amount.amount)
                .append(KEY_CATEGORY, expense.category.name)
                .append(KEY_METHOD, expense.method.name)
                .append(KEY_NAME, expense.name.name)
                .append(KEY_DATE, expense.date.timestamp)
    }

    fun deserialize(doc: Document): Expense {
        // TODO Can this be automated?
        val id = Id(doc.getObjectId(KEY_ID).toHexString())
        val amount = Amount(doc.get(KEY_AMOUNT, Decimal128::class.java).bigDecimalValue())
        val category = Category(doc.getString(KEY_CATEGORY))
        val method = PaymentMethod(doc.getString(KEY_METHOD))
        val name = Name(doc.getString(KEY_NAME))
        val date = Timestamp(doc.getLong(KEY_DATE))
        val tags = doc.get(KEY_TAGS, List::class.java)
                .map { tag -> Tag(tag as String) }
                .toSet()
        return Expense(id, name, amount, category, date, method, tags)
    }
}