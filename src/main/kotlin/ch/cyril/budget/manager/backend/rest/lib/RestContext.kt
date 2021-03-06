package ch.cyril.budget.manager.backend.rest.lib

interface RestContext {

    suspend fun sendResponse(contentType: String, content: String)

    suspend fun sendOk(code: Int)

    suspend fun sendError(code: Int, message: String?)

    suspend fun getBody(): String

    suspend fun getRawBody(): ByteArray

    fun getHeader(name: String): String?

    fun getPathParam(name: String): String?

    fun getQueryParams(name: String): List<String>

    fun getQueryParam(name: String): String? {
        val params = getQueryParams(name)
        return params.firstOrNull()
    }
}