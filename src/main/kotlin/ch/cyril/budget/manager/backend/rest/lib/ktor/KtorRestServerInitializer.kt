package ch.cyril.budget.manager.backend.rest.lib.ktor

import ch.cyril.budget.manager.backend.main.Config
import ch.cyril.budget.manager.backend.main.ServerConfig
import ch.cyril.budget.manager.backend.rest.RestServerInitializer
import ch.cyril.budget.manager.backend.rest.lib.RestParamParser
import ch.cyril.budget.manager.backend.rest.lib.RestServer
import ch.cyril.budget.manager.backend.service.ServiceFactory
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

class KtorRestServerInitializer(serviceFactory: ServiceFactory, config: ServerConfig, paramParser: RestParamParser) :
        RestServerInitializer(serviceFactory, config, paramParser) {

    override fun doStartServer(): RestServer {
        val server = embeddedServer(Netty, config.port) { }
        server.start(wait = false)
        return KtorRestServer(server)
    }
}