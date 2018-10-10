package ch.cyril.budget.manager.backend.rest.lib.javalin

import ch.cyril.budget.manager.backend.rest.lib.HttpVerb
import ch.cyril.budget.manager.backend.rest.lib.RestMethod
import ch.cyril.budget.manager.backend.rest.lib.RestServer
import io.javalin.Context
import io.javalin.Javalin
import javax.servlet.http.HttpServletResponse

class JavalinRestServer(private val javalin: Javalin) : RestServer {

    override fun register(method: RestMethod) {
        val verb = method.verb()
        val path = method.path()
        println("$verb at path '$path'")
        if (verb == HttpVerb.GET) {
            javalin.get(path) { ctx -> handle(method, ctx) }
            return
        } else if (verb == HttpVerb.POST) {
            javalin.post(path) { ctx -> handle(method, ctx) }
            return
        } else if (verb == HttpVerb.PUT) {
            javalin.put(path) { ctx -> handle(method, ctx) }
            return
        } else if (verb == HttpVerb.DELETE) {
            javalin.delete(path) { ctx -> handle(method, ctx) }
            return
        }
        throw IllegalArgumentException("Verb '$verb' not supported by Javalin")
    }

    private fun handle(method: RestMethod, ctx: Context) {
        try {
            val res = method.invoke(JavalinRestContext(ctx))
            if (res != null) {
                ctx.result(res.data)
                ctx.contentType(res.contentType)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ctx.res.sendError(HttpServletResponse.SC_BAD_REQUEST, e.message)
        }
    }
}