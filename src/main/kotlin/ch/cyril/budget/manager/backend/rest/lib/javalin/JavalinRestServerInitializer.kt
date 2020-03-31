package ch.cyril.budget.manager.backend.rest.lib.javalin

import ch.cyril.budget.manager.backend.main.ServerConfig
import ch.cyril.budget.manager.backend.rest.lib.RestServer
import ch.cyril.budget.manager.backend.rest.lib.RestServerInitializer
import io.javalin.http.Context
import io.javalin.Javalin
import io.javalin.http.staticfiles.Location
import org.eclipse.jetty.server.Server
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths

class JavalinRestServerInitializer : RestServerInitializer() {

    override fun doStartServer(config: ServerConfig): RestServer<*> {
        val javalin = Javalin.create() {
            it.server { Server(config.port) }
            if (config.staticFiles != null) {
                it.addStaticFiles(config.staticFiles.staticFilesPath, Location.EXTERNAL)
                it.addSinglePageRoot("/", config.staticFiles.indexPage, Location.EXTERNAL)
            }
        }
        return JavalinRestServer(javalin.start())
    }
}