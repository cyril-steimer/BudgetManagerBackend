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

class MongoExpenseDao(val collection: MongoCollection<Document>) : ExpenseDao {

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

    private val serialization = MongoExpenseSerialization()

    private val util = MongoUtil()

    override fun getExpenses(
            query: ExpenseQuery?,
            sort: ExpenseSort?,
            pagination: Pagination?): SubList<Expense> {
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

    override fun addExpense(expense: ExpenseWithoutId) {
        collection.insertOne(serialization.serialize(expense))
    }

    override fun updateExpense(expense: Expense) {
        val update = util.toUpdate(serialization.serialize(expense.withoutId()))
        collection.updateOne(eq(KEY_ID, ObjectId(expense.id.id)), update)
    }

    override fun deleteExpense(id: Id) {
        collection.deleteOne(eq(KEY_ID, ObjectId(id.id)))
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
}