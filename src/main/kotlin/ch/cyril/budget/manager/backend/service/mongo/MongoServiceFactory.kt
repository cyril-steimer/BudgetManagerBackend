package ch.cyril.budget.manager.backend.service.mongo

import ch.cyril.budget.manager.backend.service.ServiceFactory
import ch.cyril.budget.manager.backend.service.budget.BudgetDao
import ch.cyril.budget.manager.backend.service.expense.ExpenseDao
import ch.cyril.budget.manager.backend.service.mongo.budget.MongoBudgetDao
import ch.cyril.budget.manager.backend.service.mongo.expense.MongoExpenseDao
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import org.bson.Document

class MongoServiceFactory : ServiceFactory {

    private val expenses: MongoCollection<Document>

    private val budgets: MongoCollection<Document>

    constructor(client: MongoClient) {
        this.expenses = getCollectionInDatabase(client, "budgetManager", "expenses")
        this.budgets = getCollectionInDatabase(client, "budgetManager", "budgets")
    }

    override fun createExpenseDao(): ExpenseDao {
        return MongoExpenseDao(expenses)
    }

    override fun createBudgetDao(): BudgetDao {
        return MongoBudgetDao(budgets)
    }

    private fun getCollectionInDatabase(
            client: MongoClient,
            dbName: String,
            collectionName: String) : MongoCollection<Document> {

        val database = client.getDatabase(dbName)
        val collection = database.getCollection(collectionName)
        return collection
    }
}