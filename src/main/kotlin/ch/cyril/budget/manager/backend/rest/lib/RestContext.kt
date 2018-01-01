package ch.cyril.budget.manager.backend.rest.lib


interface RestContext {

    fun apply(result: RestResult)

    fun getBody(): String

    fun getRawBody(): ByteArray

    fun getHeader(name: String): String?

    fun getPathParam(name: String): String?

    fun getQueryParams(name: String): Array<String>

    fun getQueryParam(name: String): String? {
        val params = getQueryParams(name)
        return params.firstOrNull()
    }
}