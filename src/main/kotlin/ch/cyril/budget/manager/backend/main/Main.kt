package ch.cyril.budget.manager.backend.main

import ch.cyril.budget.manager.backend.rest.ExpenseRestHandler
import ch.cyril.budget.manager.backend.rest.GSON
import ch.cyril.budget.manager.backend.rest.lib.RestMethodRegisterer
import ch.cyril.budget.manager.backend.rest.lib.gson.GsonRestParamParser
import ch.cyril.budget.manager.backend.rest.lib.javalin.JavalinRestServer
import ch.cyril.budget.manager.backend.service.filebased.FilebasedServiceFactory
import io.javalin.Javalin
import java.nio.file.Paths


fun main(args: Array<String>) {
    val file = Paths.get("C:\\Users\\Cyril\\Projects\\BudgetManager\\budget.txt")
    val factory = FilebasedServiceFactory(file)
    val dao = factory.createExpenseDao()

    val server = Javalin.create()
            .port(8100)
            .start()
    val parser = GsonRestParamParser(GSON)
    val handler = ExpenseRestHandler(dao)

    RestMethodRegisterer(JavalinRestServer(server), parser, handler).register()
}