package ch.cyril.budget.manager.backend.main

import ch.cyril.budget.manager.backend.service.ServiceFactory
import ch.cyril.budget.manager.backend.service.filebased.FilebasedServiceFactory
import ch.cyril.budget.manager.backend.service.mongo.MongoServiceFactory
import com.mongodb.client.MongoClients
import java.nio.file.Paths

fun main(args: Array<String>) {
    val from = from()
    val to = to()

    val fromBudgetDao = from.createBudgetDao()
    val toBudgetDao = to.createBudgetDao()
    for (budget in fromBudgetDao.getBudgets().values) {
        toBudgetDao.addBudget(budget)
    }

    val fromExpenseDao = from.createExpenseDao()
    val toExpenseDao = to.createExpenseDao()
    for (expense in fromExpenseDao.getExpenses(null, null, null).values) {
        toExpenseDao.addExpense(expense)
    }
}

private fun from(): ServiceFactory {
    val expenseFile = "/Users/csteimer/projects/eduself/BudgetManager/Data/expenses.txt"
    val budgetFile = "/Users/csteimer/projects/eduself/BudgetManager/Data/budget.txt"
    return FilebasedServiceFactory(Paths.get(expenseFile), Paths.get(budgetFile))
}

private fun to(): ServiceFactory {
    val client = MongoClients.create()
    return MongoServiceFactory(client)
}