package ch.cyril.budget.manager.backend.main

import ch.cyril.budget.manager.backend.rest.BudgetRestHandler
import ch.cyril.budget.manager.backend.rest.ExpenseRestHandler
import ch.cyril.budget.manager.backend.rest.GSON
import ch.cyril.budget.manager.backend.rest.lib.RestMethodRegisterer
import ch.cyril.budget.manager.backend.rest.lib.RestParamParser
import ch.cyril.budget.manager.backend.rest.lib.RestServer
import ch.cyril.budget.manager.backend.rest.lib.gson.GsonRestParamParser
import ch.cyril.budget.manager.backend.rest.lib.ktor.KtorRestServer
import ch.cyril.budget.manager.backend.rest.lib.ktor.KtorRestServerInitializer
import com.google.gson.Gson
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import java.nio.file.Files
import java.nio.file.Paths


fun main(args: Array<String>) {
    val config = parseConfig(args)
    val factory = config.type.createServiceFactory(config.params)

    KtorRestServerInitializer(factory, config.serverConfig, GsonRestParamParser(GSON)).startServer()
}

private fun parseConfig(args: Array<String>): Config {
    if (args.isEmpty()) {
        return Config()
    }
    val configPath = Paths.get(args[0])
    return Gson().fromJson(Files.newBufferedReader(configPath), Config::class.java)
}