package ch.cyril.budget.manager.backend.main

import ch.cyril.budget.manager.backend.rest.BudgetRestHandler
import ch.cyril.budget.manager.backend.rest.ExpenseRestHandler
import ch.cyril.budget.manager.backend.rest.GSON
import ch.cyril.budget.manager.backend.rest.lib.RestMethodRegisterer
import ch.cyril.budget.manager.backend.rest.lib.gson.GsonRestParamParser
import ch.cyril.budget.manager.backend.rest.lib.javalin.JavalinRestServer
import ch.cyril.budget.manager.backend.service.filebased.FilebasedServiceFactory
import ch.cyril.budget.manager.backend.service.mongo.MongoServiceFactory
import com.mongodb.client.MongoClients
import io.javalin.Javalin
import java.nio.file.Paths


fun main(args: Array<String>) {
    val client = MongoClients.create()
    val factory = MongoServiceFactory(client)
    val expenseDao = factory.createExpenseDao()
    val budgetDao = factory.createBudgetDao()

    val server = JavalinRestServer(Javalin.create()
            .port(8100)
            .start())
    val parser = GsonRestParamParser(GSON)

    RestMethodRegisterer(server, parser, ExpenseRestHandler(expenseDao)).register()
    RestMethodRegisterer(server, parser, BudgetRestHandler(budgetDao)).register()
}