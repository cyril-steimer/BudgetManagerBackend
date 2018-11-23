package ch.cyril.budget.manager.backend.rest.lib.ktor

import ch.cyril.budget.manager.backend.rest.lib.HttpVerb
import ch.cyril.budget.manager.backend.rest.lib.RestMethod
import ch.cyril.budget.manager.backend.rest.lib.RestServer
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondBytes
import io.ktor.response.respondText
import io.ktor.routing.*
import io.ktor.server.engine.ApplicationEngine
import io.ktor.util.pipeline.PipelineContext
import kotlinx.coroutines.runBlocking
import java.lang.Exception
import java.lang.IllegalStateException

class KtorRestServer(private val engine: ApplicationEngine) : RestServer  {

    override fun register(method: RestMethod) {
        val application = engine.environment.application
        when {
            method.verb() == HttpVerb.GET -> application.routing {
                get(method.path()) {
                    handle(method, call)
                }
            }
            method.verb() == HttpVerb.POST -> application.routing {
                post(method.path()) {
                    handle(method, call)
                }
            }
            method.verb() == HttpVerb.PUT -> application.routing {
                put(method.path()) {
                    handle(method, call)
                }
            }
            method.verb() == HttpVerb.DELETE -> application.routing {
                delete(method.path()) {
                    handle(method, call)
                }
            }
            else -> throw IllegalStateException("Unknown verb ${method.verb()}")
        }
    }

    private fun handle(method: RestMethod, call: ApplicationCall) {
        try {
            val res = method.invoke(KtorRestContext(call))
            if (res != null) {
                val resString = res.data.bufferedReader().use { it.readText() }
                runBlocking {
                    //TODO Can be different content types
                    call.respondText(resString, ContentType.Application.Json)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            runBlocking {
                call.respond(HttpStatusCode.BadRequest, e.message!!)
            }
        }
    }
}