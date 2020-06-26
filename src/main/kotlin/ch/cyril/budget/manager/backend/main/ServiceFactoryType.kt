package ch.cyril.budget.manager.backend.main

import ch.cyril.budget.manager.backend.rest.GSON
import ch.cyril.budget.manager.backend.service.ServiceFactory
import ch.cyril.budget.manager.backend.service.filebased.FilebasedServiceFactory
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

class FilebasedConfig(
        val expenses: String,
        val templates: String,
        val schedules: String,
        val budget: String,
        val views: String)

enum class ServiceFactoryType {
    @SerializedName("file")
    FILE_BASED {
        override fun createServiceFactory(params: JsonObject?): ServiceFactory {
            if (params == null) {
                throw IllegalArgumentException("Cannot create file-based factory without params")
            }
            val config = GSON.fromJson(params, FilebasedConfig::class.java)
            return FilebasedServiceFactory(config.expenses, config.templates, config.schedules, config.budget, config.views)
        }
    };

    abstract fun createServiceFactory(params: JsonObject?): ServiceFactory
}