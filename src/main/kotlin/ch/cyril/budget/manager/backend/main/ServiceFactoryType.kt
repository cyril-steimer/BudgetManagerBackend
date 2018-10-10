package ch.cyril.budget.manager.backend.main

import ch.cyril.budget.manager.backend.service.ServiceFactory
import ch.cyril.budget.manager.backend.service.filebased.FilebasedServiceFactory
import ch.cyril.budget.manager.backend.service.mongo.MongoServiceFactory
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import com.mongodb.client.MongoClients
import java.lang.IllegalArgumentException
import java.nio.file.Paths

enum class ServiceFactoryType {
    @SerializedName("mongoDb")
    MONGO_DB {
        override fun createServiceFactory(params: JsonObject?): ServiceFactory {
            val client = MongoClients.create()
            return MongoServiceFactory(client)
        }
    },
    @SerializedName("file")
    FILE_BASED {
        override fun createServiceFactory(params: JsonObject?): ServiceFactory {
            if (params == null) {
                throw IllegalArgumentException("Cannot create file-based factory without params")
            }
            val expenses = Paths.get(params.get("expenses").asString)
            val budget = Paths.get(params.get("budget").asString)
            return FilebasedServiceFactory(expenses, budget)
        }
    };

    abstract fun createServiceFactory(params: JsonObject?): ServiceFactory
}