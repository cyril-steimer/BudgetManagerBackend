package ch.cyril.budget.manager.backend.rest.lib.ktor

import ch.cyril.budget.manager.backend.rest.lib.Handler
import ch.cyril.budget.manager.backend.rest.lib.RestContext
import ch.cyril.budget.manager.backend.rest.lib.RestMethodPath
import ch.cyril.budget.manager.backend.rest.lib.RestServer
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.routing.*
import io.ktor.server.engine.ApplicationEngine
import java.util.concurrent.TimeUnit

class KtorRestServer(private val engine: ApplicationEngine) : RestServer<ApplicationCall>() {

    override fun registerGet(path: String, handler: Handler<ApplicationCall>) {
        engine.environment.application.routing {
            get(path) {
                handler.invoke(call)
            }
        }
    }

    override fun registerPost(path: String, handler: Handler<ApplicationCall>) {
        engine.environment.application.routing {
            post(path) {
                handler.invoke(call)
            }
        }
    }

    override fun registerPut(path: String, handler: Handler<ApplicationCall>) {
        engine.environment.application.routing {
            put(path) {
                handler.invoke(call)
            }
        }
    }

    override fun registerDelete(path: String, handler: Handler<ApplicationCall>) {
        engine.environment.application.routing {
            delete(path) {
                handler.invoke(call)
            }
        }
    }

    override fun toPath(path: RestMethodPath): String {
        return path.toPath("{", "}")
    }

    override fun getRestContext(ctx: ApplicationCall): RestContext {
        return KtorRestContext(ctx)
    }

    override fun close() {
        engine.stop(0, 0)
    }
}