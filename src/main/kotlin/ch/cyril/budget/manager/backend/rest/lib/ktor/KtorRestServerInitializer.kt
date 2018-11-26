package ch.cyril.budget.manager.backend.rest.lib.ktor

import ch.cyril.budget.manager.backend.main.ServerConfig
import ch.cyril.budget.manager.backend.rest.lib.RestServerInitializer
import ch.cyril.budget.manager.backend.rest.lib.RestServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

class KtorRestServerInitializer : RestServerInitializer() {

    override fun doStartServer(config: ServerConfig): RestServer<*> {
        val server = embeddedServer(Netty, config.port) { }
        server.start(wait = false)
        return KtorRestServer(server)
    }
}