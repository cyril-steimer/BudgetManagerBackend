package ch.cyril.budget.manager.backend.main

import com.google.gson.JsonObject

data class Config(val type: ServiceFactoryType = ServiceFactoryType.MONGO_DB, val params: JsonObject? = null)