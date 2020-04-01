package ch.cyril.budget.manager.backend.rest

import ch.cyril.budget.manager.backend.util.gson.NullSafeTypeAdapterFactory
import ch.cyril.budget.manager.backend.util.gson.ValidatingTypeAdapterFactory
import com.google.gson.GsonBuilder

val GSON_BUILDER = GsonBuilder()
        .registerTypeAdapterFactory(NullSafeTypeAdapterFactory())
        .registerTypeAdapterFactory(ValidatingTypeAdapterFactory())

val GSON = GSON_BUILDER.create()