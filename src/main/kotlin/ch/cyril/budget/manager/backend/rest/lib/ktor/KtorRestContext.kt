package ch.cyril.budget.manager.backend.rest.lib.ktor

import ch.cyril.budget.manager.backend.rest.lib.RestContext
import io.ktor.application.ApplicationCall
import io.ktor.request.header
import io.ktor.request.receiveText
import kotlinx.coroutines.runBlocking
import java.nio.charset.StandardCharsets

class KtorRestContext(private val call: ApplicationCall) : RestContext {

    override fun getBody(): String {
        return runBlocking {
            call.receiveText()
        }
    }

    override fun getRawBody(): ByteArray {
        val body = getBody()
        return body.toByteArray(StandardCharsets.UTF_8)
    }

    override fun getHeader(name: String): String? {
        return call.request.header(name)
    }

    override fun getPathParam(name: String): String? {
        return call.parameters[name]
    }

    override fun getQueryParams(name: String): List<String> {
        val res = call.request.queryParameters.getAll(name)
        if (res == null) {
            return emptyList()
        }
        return res
    }
}