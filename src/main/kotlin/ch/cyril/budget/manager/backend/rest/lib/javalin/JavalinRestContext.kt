package ch.cyril.budget.manager.backend.rest.lib.javalin

import ch.cyril.budget.manager.backend.rest.lib.RestContext
import ch.cyril.budget.manager.backend.rest.lib.RestResult
import io.javalin.Context

class JavalinRestContext(val ctx: Context) : RestContext {

    override fun apply(result: RestResult) {
        ctx.contentType(result.contentType)
        ctx.result(result.data)
    }

    override fun getBody(): String {
        return ctx.body()
    }

    override fun getRawBody(): ByteArray {
        return ctx.bodyAsBytes()
    }

    override fun getHeader(name: String): String? {
        return ctx.header(name)
    }

    override fun getPathParam(name: String): String? {
        return ctx.param(name)
    }

    override fun getQueryParams(name: String): Array<String> {
        val params = ctx.queryParams(name)
        return params ?: emptyArray()
    }
}