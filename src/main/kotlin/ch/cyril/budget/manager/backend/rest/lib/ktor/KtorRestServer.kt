package ch.cyril.budget.manager.backend.rest.lib.ktor

import ch.cyril.budget.manager.backend.rest.lib.HttpVerb
import ch.cyril.budget.manager.backend.rest.lib.RestMethod
import ch.cyril.budget.manager.backend.rest.lib.RestServer
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.*
import io.ktor.server.engine.ApplicationEngine

class KtorRestServer(private val engine: ApplicationEngine) : RestServer  {

    override fun register(method: RestMethod) {
        val path = method.path().toPath("{", "}")
        engine.environment.application.routing {
            when (method.verb()) {
                HttpVerb.GET -> get(path) {
                    handle(method, call)
                }
                HttpVerb.POST -> post(path) {
                    handle(method, call)
                }
                HttpVerb.PUT -> put(path) {
                    handle(method, call)
                }
                HttpVerb.DELETE -> delete(path) {
                    handle(method, call)
                }
            }
        }
    }

    private suspend fun handle(method: RestMethod, call: ApplicationCall) {
        try {
            val res = method.invoke(KtorRestContext(call))
            if (res != null) {
                call.respondText(res.content, ContentType.parse(res.contentType))
            } else {
                call.respond(HttpStatusCode.OK, "")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            call.respond(HttpStatusCode.BadRequest, e.message ?: "")
        }
    }
}