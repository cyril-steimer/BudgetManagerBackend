package ch.cyril.budget.manager.backend.rest.lib

import kotlin.reflect.KFunction

class RestHandlerMethod(val function: KFunction<*>, val verb: HttpVerb, val path: String)

interface RestHandler {

    fun getHandlerMethods(): List<RestHandlerMethod>
}