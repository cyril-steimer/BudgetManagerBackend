package ch.cyril.budget.manager.backend.rest.lib.javalin

import ch.cyril.budget.manager.backend.rest.lib.RestContext
import io.javalin.http.Context

class JavalinRestContext(val ctx: Context) : RestContext {

    override suspend fun sendResponse(contentType: String, content: String) {
        ctx.result(content)
        ctx.contentType(contentType)
    }

    override suspend fun sendOk(code: Int) {
        ctx.status(code)
    }

    override suspend fun sendError(code: Int, message: String?) {
        ctx.res.sendError(code, message ?: "")
    }

    override suspend fun getBody(): String {
        return ctx.body()
    }

    override suspend fun getRawBody(): ByteArray {
        return ctx.bodyAsBytes()
    }

    override fun getHeader(name: String): String? {
        return ctx.header(name)
    }

    override fun getPathParam(name: String): String? {
        return ctx.pathParam(name)
    }

    override fun getQueryParams(name: String): List<String> {
        return ctx.queryParams(name)
    }
}