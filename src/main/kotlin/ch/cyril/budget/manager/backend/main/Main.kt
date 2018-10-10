package ch.cyril.budget.manager.backend.main

import ch.cyril.budget.manager.backend.rest.BudgetRestHandler
import ch.cyril.budget.manager.backend.rest.ExpenseRestHandler
import ch.cyril.budget.manager.backend.rest.GSON
import ch.cyril.budget.manager.backend.rest.lib.RestMethodRegisterer
import ch.cyril.budget.manager.backend.rest.lib.gson.GsonRestParamParser
import ch.cyril.budget.manager.backend.rest.lib.javalin.JavalinRestServer
import ch.cyril.budget.manager.backend.service.filebased.FilebasedServiceFactory
import ch.cyril.budget.manager.backend.service.mongo.MongoServiceFactory
import com.google.gson.Gson
import com.mongodb.client.MongoClients
import io.javalin.Javalin
import java.io.FileReader
import java.nio.file.Files
import java.nio.file.Paths


fun main(args: Array<String>) {
    val config = parseConfig(args)
    val factory = config.type.createServiceFactory(config.params)
    val expenseDao = factory.createExpenseDao()
    val budgetDao = factory.createBudgetDao()

    val server = JavalinRestServer(Javalin.create()
            .port(8100)
            .start())
    val parser = GsonRestParamParser(GSON)

    RestMethodRegisterer(server, parser, ExpenseRestHandler(expenseDao)).register()
    RestMethodRegisterer(server, parser, BudgetRestHandler(budgetDao)).register()
}

private fun parseConfig (args: Array<String>): Config {
    if (args.isEmpty()) {
        return Config()
    }
    val configPath = Paths.get(args[0])
    return Gson().fromJson(Files.newBufferedReader(configPath), Config::class.java)
}