package ch.cyril.budget.manager.backend.main

import ch.cyril.budget.manager.backend.service.ServiceFactory
import ch.cyril.budget.manager.backend.service.filebased.FilebasedServiceFactory
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import java.nio.file.Paths

enum class ServiceFactoryType {
    @SerializedName("file")
    FILE_BASED {
        override fun createServiceFactory(params: JsonObject?): ServiceFactory {
            if (params == null) {
                throw IllegalArgumentException("Cannot create file-based factory without params")
            }
            val expenses = Paths.get(params.get("expenses").asString)
            val templates = Paths.get(params.get("templates").asString)
            val scheduled = Paths.get(params.get("schedules").asString)
            val budget = Paths.get(params.get("budget").asString)
            return FilebasedServiceFactory(expenses, templates, scheduled, budget)
        }
    };

    abstract fun createServiceFactory(params: JsonObject?): ServiceFactory
}