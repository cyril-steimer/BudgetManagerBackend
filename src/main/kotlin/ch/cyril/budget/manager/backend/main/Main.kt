package ch.cyril.budget.manager.backend.main

import ch.cyril.budget.manager.backend.rest.ExpenseRestHandler
import ch.cyril.budget.manager.backend.rest.GSON
import ch.cyril.budget.manager.backend.rest.lib.RestHandlerInvoker
import ch.cyril.budget.manager.backend.rest.lib.gson.GsonRestParamParser
import ch.cyril.budget.manager.backend.rest.lib.javalin.JavalinRestContext
import ch.cyril.budget.manager.backend.service.filebased.FilebasedServiceFactory
import io.javalin.Context
import io.javalin.Javalin
import java.nio.file.Paths
import javax.servlet.http.HttpServletResponse
import kotlin.reflect.KFunction

val invoker = RestHandlerInvoker(GsonRestParamParser(GSON))

fun main(args: Array<String>) {
    val file = Paths.get("C:\\Users\\Cyril\\Projects\\BudgetManager\\budget.txt")
    val factory = FilebasedServiceFactory(file)
    val dao = factory.createExpenseDao()
    val handler = ExpenseRestHandler(dao)

    val app = Javalin.create()
            .port(8100)
            .start()

    app.get("/api/v1/expenses") { ctx -> handle(handler::getAllExpenses, ctx) }
    app.get("/api/v1/expenses/search/:arg") { ctx -> handle(handler::search, ctx) }
    app.get("/api/v1/expenses/:query/:arg") { ctx -> handle(handler::handleSimpleQuery, ctx) }

    app.post("/api/v1/expenses") { ctx -> handle(handler::addExpense, ctx) }

    app.put("/api/v1/expenses") { ctx -> handle(handler::updateExpense, ctx) }

    app.delete("/api/v1/expenses") { ctx -> handle(handler::deleteExpense, ctx) }
}

fun handle(function: KFunction<*>, ctx: Context) {
    try {
        invoker.invoke(function, JavalinRestContext(ctx))
    } catch (e: Throwable) {
        e.printStackTrace()
        ctx.response().sendError(HttpServletResponse.SC_BAD_REQUEST, e.message)
    }
}