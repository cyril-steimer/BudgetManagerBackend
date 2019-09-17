package ch.cyril.budget.manager.backend.rest

import ch.cyril.budget.manager.backend.util.gson.NullSafeTypeAdapterFactory
import ch.cyril.budget.manager.backend.util.gson.ValidatingTypeAdapterFactory
import com.google.gson.GsonBuilder

val GSON = GsonBuilder()
        .registerTypeAdapterFactory(NullSafeTypeAdapterFactory())
        .registerTypeAdapterFactory(ValidatingTypeAdapterFactory())
        .create()