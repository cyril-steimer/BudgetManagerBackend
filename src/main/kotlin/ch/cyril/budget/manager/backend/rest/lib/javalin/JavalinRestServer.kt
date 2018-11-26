package ch.cyril.budget.manager.backend.rest.lib.javalin

import ch.cyril.budget.manager.backend.rest.lib.HttpVerb
import ch.cyril.budget.manager.backend.rest.lib.RestMethod
import ch.cyril.budget.manager.backend.rest.lib.RestServer
import io.javalin.Context
import io.javalin.Javalin
import kotlinx.coroutines.runBlocking
import javax.servlet.http.HttpServletResponse

class JavalinRestServer(private val javalin: Javalin) : RestServer {

    override fun register(method: RestMethod) {
        val path = method.path().toPath(":", "")
        when (method.verb()) {
            HttpVerb.GET -> {
                javalin.get(path) { ctx -> handle(method, ctx) }
            }
            HttpVerb.POST -> {
                javalin.post(path) { ctx -> handle(method, ctx) }
            }
            HttpVerb.PUT -> {
                javalin.put(path) { ctx -> handle(method, ctx) }
            }
            HttpVerb.DELETE -> {
                javalin.delete(path) { ctx -> handle(method, ctx) }
            }
        }
    }

    private fun handle(method: RestMethod, ctx: Context) {
        try {
            runBlocking {
                val res = method.invoke(JavalinRestContext(ctx))
                if (res != null) {
                    ctx.result(res.content)
                    ctx.contentType(res.contentType)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ctx.res.sendError(HttpServletResponse.SC_BAD_REQUEST, e.message)
        }
    }
}