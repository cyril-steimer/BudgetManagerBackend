package ch.cyril.budget.manager.backend.main

import ch.cyril.budget.manager.backend.rest.lib.RestServerInitializer
import ch.cyril.budget.manager.backend.rest.lib.javalin.JavalinRestServerInitializer
import ch.cyril.budget.manager.backend.rest.lib.ktor.KtorRestServerInitializer
import com.google.gson.JsonObject

data class Config(
        val type: ServiceFactoryType = ServiceFactoryType.FILE_BASED,
        val params: JsonObject? = null,
        val server: ServerType = ServerType.KTOR,
        val serverConfig: ServerConfig = ServerConfig())

enum class ServerType(val serverInitializer: RestServerInitializer) {
    KTOR(KtorRestServerInitializer()),
    JAVALIN(JavalinRestServerInitializer())
}

data class ServerConfig(
        val port: Int = 80,
        val staticFiles: StaticFiles? = null)

data class StaticFiles(
        val staticFilesPath: String,
        val indexPage: String)