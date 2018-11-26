package ch.cyril.budget.manager.backend.rest.lib.javalin

import ch.cyril.budget.manager.backend.main.ServerConfig
import ch.cyril.budget.manager.backend.rest.lib.RestServerInitializer
import ch.cyril.budget.manager.backend.rest.lib.RestServer
import io.javalin.Javalin

class JavalinRestServerInitializer : RestServerInitializer() {

    override fun doStartServer(config: ServerConfig): RestServer<*> {
        val javalin = Javalin.create().start(config.port)
        return JavalinRestServer(javalin)
    }
}