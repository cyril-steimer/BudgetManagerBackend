package ch.cyril.budget.manager.backend.rest.lib

enum class HttpVerb {
    GET, POST, PUT, DELETE
}

annotation class HttpMethod(val verb: HttpVerb, val path: String)

annotation class Body

annotation class Header(val name: String)

annotation class PathParam(val name: String)

annotation class QueryParam(val name: String)