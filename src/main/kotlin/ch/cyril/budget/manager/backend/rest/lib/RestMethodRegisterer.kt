package ch.cyril.budget.manager.backend.rest.lib

import kotlin.reflect.full.memberFunctions

class RestMethodRegisterer(
        val server: RestServer<*>,
        val parser: RestParamParser,
        val handler: Any) {

    fun register() {
        getRestMethods()
                .filter { m -> m != null }
                .forEach { m -> registerMethod(m!!) }
    }

    private fun getRestMethods(): List<RestMethod?> {
        if (handler is RestHandler) {
            return handler.getHandlerMethods()
                    .map { m -> RestMethod.of(handler, m, parser) }
        }
        return handler::class.memberFunctions
                .map { f -> RestMethod.of(handler, f, parser) }
    }

    private fun registerMethod(method: RestMethod) {
        println("${method.verb()} at path '${method.path()}'")
        server.register(method)
    }
}