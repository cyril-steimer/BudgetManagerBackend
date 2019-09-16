package ch.cyril.budget.manager.backend.service.mongo.expense

import ch.cyril.budget.manager.backend.model.*
import ch.cyril.budget.manager.backend.service.mongo.*
import org.bson.Document
import org.bson.types.Decimal128
import java.time.DayOfWeek

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

class MongoExpenseTemplateSerialization : MongoExpenseSerialization<ExpenseTemplate, ExpenseTemplateWithoutId>() {

    override fun doDeserialize(doc: Document, id: Id, amount: Amount, category: Category, method: PaymentMethod, name: Name, author: Author, tags: Set<Tag>): ExpenseTemplate {
        return ExpenseTemplate(id, name, amount, category, method, author, tags)
    }
}

class SerializingScheduleVisitor : ScheduleVisitor<Document, Document> {

    override fun visitWeeklySchedule(schedule: WeeklySchedule, arg: Document): Document {
        return arg.append(KEY_WEEKLY, schedule.dayOfWeek.value)
    }

    override fun visitMonthlySchedule(schedule: MonthlySchedule, arg: Document): Document {
        return arg.append(KEY_MONTHLY, schedule.dayOfMonth)
    }
}

// TODO Test this
class MongoScheduledExpenseSerialization : MongoExpenseSerialization<ScheduledExpense, ScheduledExpenseWithoutId>() {

    private val expenseSerialization = MongoActualExpenseSerialization()

    override fun serialize(expense: ScheduledExpenseWithoutId): Document {
        val res = super.serialize(expense)
        res.append(KEY_START_DATE, expense.startDate.timestamp)
        res.append(KEY_END_DATE, expense.endDate.timestamp)
        expense.schedule.accept(SerializingScheduleVisitor(), res)
        if (expense.lastExpense != null) {
            res.append(KEY_LAST_UPDATE, expenseSerialization.serialize(expense.lastExpense.withoutId()))
        }
        return res
    }

    override fun doDeserialize(doc: Document, id: Id, amount: Amount, category: Category, method: PaymentMethod, name: Name, author: Author, tags: Set<Tag>): ScheduledExpense {
        val schedule = deserializeSchedule(doc)
        var lastExpense: ActualExpense? = null
        val startDate = Timestamp(doc.getLong(KEY_START_DATE))
        val endDate = Timestamp(doc.getLong(KEY_END_DATE))
        if (doc.containsKey(KEY_LAST_UPDATE)) {
            lastExpense = expenseSerialization.deserialize(doc.get(KEY_LAST_UPDATE, Document::class.java))
        }
        return ScheduledExpense(id, name, amount, category, method, author, tags, startDate, endDate, schedule, lastExpense)
    }

    private fun deserializeSchedule(doc: Document): Schedule {
        if (doc.containsKey(KEY_WEEKLY)) {
            return WeeklySchedule(DayOfWeek.of(doc.getInteger(KEY_WEEKLY)))
        }
        return MonthlySchedule(doc.getInteger(KEY_MONTHLY))
    }
}