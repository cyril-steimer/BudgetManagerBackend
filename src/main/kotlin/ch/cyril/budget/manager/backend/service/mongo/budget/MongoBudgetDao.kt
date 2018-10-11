package ch.cyril.budget.manager.backend.service.mongo.budget

import ch.cyril.budget.manager.backend.model.*
import ch.cyril.budget.manager.backend.service.budget.BudgetDao
import ch.cyril.budget.manager.backend.service.mongo.*
import ch.cyril.budget.manager.backend.util.SubList
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters.*
import com.mongodb.client.model.UpdateOptions
import org.bson.Document

class MongoBudgetDao(val collection: MongoCollection<Document>) : BudgetDao {

    private val serialization = MongoBudgetSerialization()

    private val util = MongoUtil()

    override fun getCategories(): SubList<Category> {
        val categories = getBudgets().values
                .map { budget -> budget.category }
                .toList()
        return SubList.of(categories)
    }

    override fun getBudgets(): SubList<Budget> {
        val budgets = collection.find()
                .map { doc -> serialization.deserialize(doc) }
                .toList()
        return SubList.of(budgets)
    }

    override fun addBudget(budget: Budget) {
        collection.insertOne(serialization.serialize(budget))
    }

    override fun updateBudget(budget: Budget) {
        collection.updateOne(
                eq(KEY_ID, budget.category.name),
                util.toUpdate(serialization.serialize(budget)))
    }

    override fun deleteBudget(budget: Budget) {
        collection.deleteOne(eq(KEY_ID, budget.category.name))
    }
}