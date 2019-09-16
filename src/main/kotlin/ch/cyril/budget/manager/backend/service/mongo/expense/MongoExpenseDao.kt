package ch.cyril.budget.manager.backend.service.mongo.expense

import ch.cyril.budget.manager.backend.model.*
import ch.cyril.budget.manager.backend.service.Pagination
import ch.cyril.budget.manager.backend.service.expense.*
import ch.cyril.budget.manager.backend.service.mongo.*
import ch.cyril.budget.manager.backend.util.SubList
import com.mongodb.client.FindIterable
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Sorts.ascending
import com.mongodb.client.model.Sorts.descending
import org.bson.Document
import org.bson.conversions.Bson
import org.bson.types.ObjectId

abstract class MongoExpenseDao<T : Expense>(
        protected val collection: MongoCollection<Document>,
        protected val serialization: MongoExpenseSerialization<T>) : ExpenseDao<T> {

    private class MongoSortDirectionSwitch : SortDirectionSwitch<String, Bson> {

        override fun caseAscending(arg: String): Bson {
            return ascending(arg)
        }

        override fun caseDescending(arg: String): Bson {
            return descending(arg)
        }
    }

    private class MongoSortFieldSwitch : ExpenseSortFieldSwitch<Unit, String> {

        override fun caseId(arg: Unit): String {
            return KEY_ID
        }

        override fun caseAmount(arg: Unit): String {
            return KEY_AMOUNT
        }

        override fun caseName(arg: Unit): String {
            return KEY_NAME
        }

        override fun caseCategory(arg: Unit): String {
            return KEY_CATEGORY
        }

        override fun caseDate(arg: Unit): String {
            return KEY_DATE
        }
    }

    private val visitor = MongoExpenseQueryVisitor()

    private val util = MongoUtil()

    override fun getExpenses(
            query: ExpenseQuery?,
            sort: ExpenseSort?,
            pagination: Pagination?): SubList<T> {
        val iterable: FindIterable<Document>
        if (query != null) {
            iterable = collection.find(query.accept(visitor, Unit))
        } else {
            iterable = collection.find()
        }
        if (sort != null) {
            val field = sort.field.switch(MongoSortFieldSwitch(), Unit)
            iterable.sort(sort.direction.switch(MongoSortDirectionSwitch(), field))
        }
        if (pagination != null) {
            iterable.skip(pagination.from).limit(pagination.count)
        }
        val list = iterable.map { d -> serialization.deserialize(d) }.toList()
        return SubList.of(list)
    }

    override fun updateExpense(expense: T) {
        val update = util.toUpdate(serialization.serialize(expense))
        collection.updateOne(eq(KEY_ID, ObjectId(expense.id.id)), update)
    }

    override fun deleteExpense(id: Id) {
        collection.deleteOne(eq(KEY_ID, ObjectId(id.id)))
    }
}

class MongoActualExpenseDao(collection: MongoCollection<Document>) :
        MongoExpenseDao<ActualExpense>(collection, MongoActualExpenseSerialization()),
        ActualExpenseDao {

    override fun addExpense(expense: ActualExpenseWithoutId): ActualExpense {
        val withId = expense.withId(Id(ObjectId().toHexString()))
        collection.insertOne(serialization.serialize(withId))
        return withId
    }

    override fun getPaymentMethods(): Set<PaymentMethod> {
        return collection.distinct(KEY_METHOD, String::class.java)
                .map { value -> PaymentMethod(value) }
                .toSet()
    }

    override fun getTags(): Set<Tag> {
        return collection.distinct(KEY_TAGS, String::class.java)
                .map { value -> Tag(value) }
                .toSet()
    }

    override fun getAuthors(): Set<Author> {
        return collection.distinct(KEY_AUTHOR, String::class.java)
                .map { value -> Author(value) }
                .toSet()
    }
}

class MongoExpenseTemplateDao(collection: MongoCollection<Document>) :
        MongoExpenseDao<ExpenseTemplate>(collection, MongoExpenseTemplateSerialization()),
        ExpenseTemplateDao {

    override fun addExpense(expense: ExpenseTemplateWithoutId): ExpenseTemplate {
        val withId = expense.withId(Id(ObjectId().toHexString()))
        collection.insertOne(serialization.serialize(withId))
        return withId
    }
}

class MongoScheduledExpenseDao(collection: MongoCollection<Document>) :
        MongoExpenseDao<ScheduledExpense>(collection, MongoScheduledExpenseSerialization()),
        ScheduledExpenseDao {

    override fun addExpense(expense: ScheduledExpenseWithoutId): ScheduledExpense {
        val withId = expense.withId(Id(ObjectId().toHexString()))
        collection.insertOne(serialization.serialize(withId))
        return withId
    }
}