package ch.cyril.budget.manager.backend.rest.lib.ktor

import ch.cyril.budget.manager.backend.rest.lib.RestContext
import io.ktor.application.ApplicationCall
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.withCharset
import io.ktor.request.contentCharset
import io.ktor.request.header
import io.ktor.request.receiveStream
import io.ktor.response.respond
import io.ktor.response.respondText

class KtorRestContext(private val call: ApplicationCall) : RestContext {

    override suspend fun sendResponse(contentType: String, content: String) {
        call.respondText(content, ContentType.parse(contentType).withCharset(Charsets.UTF_8))
    }

    override suspend fun sendOk(code: Int) {
        call.respond(HttpStatusCode.fromValue(code), "")
    }

    override suspend fun sendError(code: Int, message: String?) {
        call.respond(HttpStatusCode.fromValue(code), message ?: "")
    }

    override suspend fun getBody(): String {
        val charset = call.request.contentCharset() ?: Charsets.UTF_8
        return call.receiveStream().reader(charset).readText();
    }

    override suspend fun getRawBody(): ByteArray {
        return call.receiveStream().readBytes()
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