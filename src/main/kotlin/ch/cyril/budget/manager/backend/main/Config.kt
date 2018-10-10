package ch.cyril.budget.manager.backend.main

import com.google.gson.JsonObject

data class Config(
        val type: ServiceFactoryType = ServiceFactoryType.MONGO_DB,
        val params: JsonObject? = null,
        val serverConfig: ServerConfig = ServerConfig())

data class ServerConfig(
        val port: Int = 80,
        val staticFiles: StaticFiles? = null)

data class StaticFiles(
        val staticFilesPath: String,
        val indexPage: String)