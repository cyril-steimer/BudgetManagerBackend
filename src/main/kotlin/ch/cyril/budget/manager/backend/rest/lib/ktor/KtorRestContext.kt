package ch.cyril.budget.manager.backend.rest.lib.ktor

import ch.cyril.budget.manager.backend.rest.lib.RestContext
import io.ktor.application.ApplicationCall
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.header
import io.ktor.request.receiveText
import io.ktor.response.respond
import io.ktor.response.respondText
import java.nio.charset.StandardCharsets

class KtorRestContext(private val call: ApplicationCall) : RestContext {

    override suspend fun sendResponse(contentType: String, content: String) {
        call.respondText(content, ContentType.parse(contentType))
    }

    override suspend fun sendOk(code: Int) {
        call.respond(HttpStatusCode.fromValue(code), "")
    }

    override suspend fun sendError(code: Int, message: String?) {
        call.respond(HttpStatusCode.fromValue(code), message ?: "")
    }

    override suspend fun getBody(): String {
        return call.receiveText()
    }

    override suspend fun getRawBody(): ByteArray {
        return getBody().toByteArray(StandardCharsets.UTF_8)
    }

    override fun getHeader(name: String): String? {
        return call.request.header(name)
    }

    override fun getPathParam(name: String): String? {
        return call.parameters[name]
    }

    override fun getQueryParams(name: String): List<String> {
        val res = call.request.queryParameters.getAll(name)
        return res ?: emptyList()
    }
}