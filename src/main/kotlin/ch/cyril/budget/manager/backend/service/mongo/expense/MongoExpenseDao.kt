package ch.cyril.budget.manager.backend.service.mongo.expense

import ch.cyril.budget.manager.backend.model.Expense
import ch.cyril.budget.manager.backend.model.Id
import ch.cyril.budget.manager.backend.model.PaymentMethod
import ch.cyril.budget.manager.backend.model.Tag
import ch.cyril.budget.manager.backend.service.Pagination
import ch.cyril.budget.manager.backend.service.expense.ExpenseDao
import ch.cyril.budget.manager.backend.service.expense.ExpenseQuery
import ch.cyril.budget.manager.backend.service.expense.ExpenseSort
import ch.cyril.budget.manager.backend.service.mongo.KEY_ID
import ch.cyril.budget.manager.backend.service.mongo.MongoUtil
import ch.cyril.budget.manager.backend.util.SubList
import com.mongodb.client.FindIterable
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters.*
import org.bson.Document

class MongoExpenseDao(val collection: MongoCollection<Document>) : ExpenseDao {

    private val visitor = MongoExpenseQueryVisitor()

    private val serialization = MongoExpenseSerialization()

    private val util = MongoUtil()

    override fun getExpenses(
            query: ExpenseQuery?,
            sort: ExpenseSort?,
            pagination: Pagination?): SubList<Expense> {
        //TODO Sorting and Pagination
        val iterable: FindIterable<Document>
        if (query != null) {
            iterable = collection.find(query.accept(visitor, Unit))
        } else {
            iterable = collection.find()
        }
        val list = iterable.map { d -> serialization.deserialize(d) }.toList()
        return SubList.of(list)
    }

    override fun addExpense(e: Expense) {
        val insert = Expense(getNewId(), e.name, e.amount, e.category, e.date, e.method, e.tags)
        collection.insertOne(serialization.serialize(insert))
    }

    override fun updateExpense(expense: Expense) {
        val update = util.toUpdate(serialization.serialize(expense))
        collection.updateOne(eq(KEY_ID, expense.id.id), update)
    }

    override fun deleteExpense(expense: Expense) {
        collection.deleteOne(eq(KEY_ID, expense.id.id))
    }

    override fun getPaymentMethods(): Set<PaymentMethod> {
        //TODO Optimize
        return getExpenses(null, null, null).values
                .map { e -> e.method }
                .toSet()
    }

    override fun getTags(): Set<Tag> {
        //TODO Optimize
        return getExpenses(null, null, null).values
                .flatMap { e -> e.tags }
                .toSet()
    }

    private fun getNewId(): Id {
        //TODO Better calculatiuon....
        val max = getExpenses(null, null, null).values
                .map { e -> e.id.id }
                .max()
        val newId = (max ?: 0) + 1
        return Id(newId)
    }
}