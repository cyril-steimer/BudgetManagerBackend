package ch.cyril.budget.manager.backend.main

import ch.cyril.budget.manager.backend.rest.GSON
import ch.cyril.budget.manager.backend.rest.lib.gson.GsonRestParamParser
import ch.cyril.budget.manager.backend.rest.lib.ktor.KtorRestServerInitializer
import com.google.gson.Gson
import java.nio.file.Files
import java.nio.file.Paths


fun main(args: Array<String>) {
    val config = parseConfig(args)
    val factory = config.type.createServiceFactory(config.params)

    val initializer = KtorRestServerInitializer(factory, config.serverConfig, GsonRestParamParser(GSON))
    initializer.startServer()
}

private fun parseConfig(args: Array<String>): Config {
    if (args.isEmpty()) {
        return Config()
    }
    val configPath = Paths.get(args[0])
    return Gson().fromJson(Files.newBufferedReader(configPath), Config::class.java)
}