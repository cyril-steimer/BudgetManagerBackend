package ch.cyril.budget.manager.backend.service.mongo.expense

import ch.cyril.budget.manager.backend.model.*
import ch.cyril.budget.manager.backend.service.mongo.*
import org.bson.Document
import org.bson.types.Decimal128

abstract class MongoExpenseSerialization<T1 : Expense, T2: ExpenseWithoutId> {

    open fun serialize(expense: T2): Document {
        // TODO Can this be automated?
        return Document()
                .append(KEY_TAGS, expense.tags.map { tag -> tag.name })
                .append(KEY_AMOUNT, expense.amount.amount)
                .append(KEY_CATEGORY, expense.category.name)
                .append(KEY_METHOD, expense.method.name)
                .append(KEY_NAME, expense.name.name)
                .append(KEY_AUTHOR, expense.author.name)
    }

    fun deserialize(doc: Document): T1 {
        // TODO Can this be automated?
        val id = Id(doc.getObjectId(KEY_ID).toHexString())
        val amount = Amount(doc.get(KEY_AMOUNT, Decimal128::class.java).bigDecimalValue())
        val category = Category(doc.getString(KEY_CATEGORY))
        val method = PaymentMethod(doc.getString(KEY_METHOD))
        val name = Name(doc.getString(KEY_NAME))
        val author = Author(doc.getString(KEY_AUTHOR))
        val tags = doc.get(KEY_TAGS, List::class.java)
                .map { tag -> Tag(tag as String) }
                .toSet()
        return doDeserialize(doc, id, amount, category, method, name, author, tags)
    }

    protected abstract fun doDeserialize(
            doc: Document,
            id: Id,
            amount: Amount,
            category: Category,
            method: PaymentMethod,
            name: Name,
            author: Author,
            tags: Set<Tag>): T1
}

class MongoActualExpenseSerialization : MongoExpenseSerialization<ActualExpense, ActualExpenseWithoutId>() {

    override fun serialize(expense: ActualExpenseWithoutId): Document {
        return super.serialize(expense)
                .append(KEY_DATE, expense.date.timestamp)
    }

    override fun doDeserialize(doc: Document, id: Id, amount: Amount, category: Category, method: PaymentMethod, name: Name, author: Author, tags: Set<Tag>): ActualExpense {
        val date = Timestamp(doc.getLong(KEY_DATE))
        return ActualExpense(id, name, amount, category, date, method, author, tags)
    }
}

class MongoExpenseTemplateSerialization: MongoExpenseSerialization<ExpenseTemplate, ExpenseTemplateWithoutId>() {

    override fun doDeserialize(doc: Document, id: Id, amount: Amount, category: Category, method: PaymentMethod, name: Name, author: Author, tags: Set<Tag>): ExpenseTemplate {
        return ExpenseTemplate(id, name, amount, category, method, author, tags)
    }
}