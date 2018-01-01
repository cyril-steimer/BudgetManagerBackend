package ch.cyril.budget.manager.backend.rest.lib

import kotlin.reflect.KClass
import kotlin.reflect.full.memberFunctions

class RestMethodRegisterer(
        val server: RestServer,
        val parser: RestParamParser,
        val handler: Any) {

    fun register() {
        handler::class.memberFunctions
                .map { f -> RestMethod.of(handler, f, parser) }
                .filter { m -> m != null }
                .forEach { m -> server.register(m!!) }
    }
}