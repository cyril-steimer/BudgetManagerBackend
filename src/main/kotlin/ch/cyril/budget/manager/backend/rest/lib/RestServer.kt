package ch.cyril.budget.manager.backend.rest.lib

interface RestServer {

    fun register(method: RestMethod)
}