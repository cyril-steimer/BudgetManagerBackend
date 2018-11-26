package ch.cyril.budget.manager.backend.rest.lib

import kotlin.reflect.full.memberFunctions

class RestMethodRegisterer(
        val server: RestServer<*>,
        val parser: RestParamParser,
        val handler: Any) {

    fun register() {
        handler::class.memberFunctions
                .map { f -> RestMethod.of(handler, f, parser) }
                .filter { m -> m != null }
                .forEach { m -> registerMethod(m!!) }
    }

    private fun registerMethod(method: RestMethod) {
        println("${method.verb()} at path '${method.path()}'")
        server.register(method)
    }
}