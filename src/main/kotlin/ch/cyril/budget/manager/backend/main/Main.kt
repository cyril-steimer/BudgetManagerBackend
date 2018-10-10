package ch.cyril.budget.manager.backend.main

import ch.cyril.budget.manager.backend.rest.BudgetRestHandler
import ch.cyril.budget.manager.backend.rest.ExpenseRestHandler
import ch.cyril.budget.manager.backend.rest.GSON
import ch.cyril.budget.manager.backend.rest.lib.RestMethodRegisterer
import ch.cyril.budget.manager.backend.rest.lib.RestServer
import ch.cyril.budget.manager.backend.rest.lib.gson.GsonRestParamParser
import ch.cyril.budget.manager.backend.rest.lib.javalin.JavalinRestServer
import ch.cyril.budget.manager.backend.service.filebased.FilebasedServiceFactory
import ch.cyril.budget.manager.backend.service.mongo.MongoServiceFactory
import com.google.gson.Gson
import com.mongodb.client.MongoClients
import io.javalin.Context
import io.javalin.Javalin
import io.javalin.core.util.Util
import io.javalin.staticfiles.Location
import java.io.FileReader
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths


fun main(args: Array<String>) {
    val config = parseConfig(args)
    val factory = config.type.createServiceFactory(config.params)
    val expenseDao = factory.createExpenseDao()
    val budgetDao = factory.createBudgetDao()

    val server = getServer(config.serverConfig)
    val parser = GsonRestParamParser(GSON)

    RestMethodRegisterer(server, parser, ExpenseRestHandler(expenseDao)).register()
    RestMethodRegisterer(server, parser, BudgetRestHandler(budgetDao)).register()
}

private fun parseConfig(args: Array<String>): Config {
    if (args.isEmpty()) {
        return Config()
    }
    val configPath = Paths.get(args[0])
    return Gson().fromJson(Files.newBufferedReader(configPath), Config::class.java)
}

private fun getServer(config: ServerConfig): RestServer {
    val javalin = Javalin.create().port(config.port)
    if (config.staticFiles != null) {
        javalin.enableStaticFiles(config.staticFiles.staticFilesPath, Location.EXTERNAL)
        val html = String(Files.readAllBytes(Paths.get(config.staticFiles.indexPage)), StandardCharsets.UTF_8)
        javalin.after { ctx -> enableSinglePageMode(ctx, html) }
    }
    return JavalinRestServer(javalin.start())
}

private fun enableSinglePageMode(ctx: Context, html: String) {
    if (ctx.res.status == 404) {
        // This ensures that whenever we would actually return a '404', we instead return 'index.html'.
        // Angular can then work its magic to show the correct page within its application.
        ctx.res.reset()
        ctx.html(html)
    }
}