package ch.cyril.budget.manager.backend.rest.lib

import javax.servlet.http.HttpServletResponse

abstract class RestServer<T> {

    fun register(method: RestMethod) {
        val path = toPath(method.path())
        when (method.verb()) {
            HttpVerb.GET -> registerGet(path) { ctx -> handle(method, ctx) }
            HttpVerb.POST -> registerPost(path) { ctx -> handle(method, ctx) }
            HttpVerb.PUT -> registerPut(path) { ctx -> handle(method, ctx) }
            HttpVerb.DELETE -> registerDelete(path) { ctx -> handle(method, ctx) }
        }
    }

    protected abstract fun registerGet(path: String, handler: Handler<T>)

    protected abstract fun registerPost(path: String, handler: Handler<T>)

    protected abstract fun registerPut(path: String, handler: Handler<T>)

    protected abstract fun registerDelete(path: String, handler: Handler<T>)

    protected abstract fun toPath(path: RestMethodPath): String

    protected abstract fun getRestContext(ctx: T): RestContext

    private suspend fun handle(method: RestMethod, ctx: T) {
        val restContext = getRestContext(ctx)
        try {
            val res = method.invoke(restContext)
            if (res != null) {
                restContext.sendResponse(res.contentType, res.content)
            } else {
                restContext.sendOk(HttpServletResponse.SC_OK)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            restContext.sendError(HttpServletResponse.SC_BAD_REQUEST, e.message)
        }
    }
}

typealias Handler<T> = suspend (ctx: T) -> Unit