package ch.cyril.budget.manager.backend.service.mongo

import ch.cyril.budget.manager.backend.service.ServiceFactory
import ch.cyril.budget.manager.backend.service.budget.BudgetDao
import ch.cyril.budget.manager.backend.service.expense.ActualExpenseDao
import ch.cyril.budget.manager.backend.service.expense.ExpenseDao
import ch.cyril.budget.manager.backend.service.expense.ExpenseTemplateDao
import ch.cyril.budget.manager.backend.service.expense.ScheduledExpenseDao
import ch.cyril.budget.manager.backend.service.mongo.budget.MongoBudgetDao
import ch.cyril.budget.manager.backend.service.mongo.expense.MongoActualExpenseDao
import ch.cyril.budget.manager.backend.service.mongo.expense.MongoExpenseDao
import ch.cyril.budget.manager.backend.service.mongo.expense.MongoExpenseTemplateDao
import ch.cyril.budget.manager.backend.service.mongo.expense.MongoScheduledExpenseDao
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import org.bson.Document

class MongoServiceFactory(client: MongoClient) : ServiceFactory {

    private val expenses: MongoCollection<Document>
    private val templates: MongoCollection<Document>
    private val scheduledExpenses: MongoCollection<Document>
    private val budgets: MongoCollection<Document>

    init {
        this.expenses = getCollectionInDatabase(client, "budgetManager", "expenses")
        this.templates = getCollectionInDatabase(client, "budgetManager", "templates")
        this.scheduledExpenses = getCollectionInDatabase(client, "budgetManager", "scheduled")
        this.budgets = getCollectionInDatabase(client, "budgetManager", "budgets")
    }

    override fun createExpenseDao(): ActualExpenseDao {
        return MongoActualExpenseDao(expenses)
    }

    override fun createTemplateDao(): ExpenseTemplateDao {
        return MongoExpenseTemplateDao(templates)
    }

    override fun createScheduledExpenseDao(): ScheduledExpenseDao {
        return MongoScheduledExpenseDao(scheduledExpenses)
    }

    override fun createBudgetDao(): BudgetDao {
        return MongoBudgetDao(budgets)
    }

    private fun getCollectionInDatabase(
            client: MongoClient,
            dbName: String,
            collectionName: String) : MongoCollection<Document> {

        val database = client.getDatabase(dbName)
        return database.getCollection(collectionName)
    }
}