package ch.cyril.budget.manager.backend.rest.lib.ktor

import ch.cyril.budget.manager.backend.main.ServerConfig
import ch.cyril.budget.manager.backend.main.StaticFiles
import ch.cyril.budget.manager.backend.rest.lib.RestServer
import ch.cyril.budget.manager.backend.rest.lib.RestServerInitializer
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.request.uri
import io.ktor.response.respondBytes
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import java.nio.file.Files
import java.nio.file.Paths

class KtorRestServerInitializer : RestServerInitializer() {

    override fun doStartServer(config: ServerConfig): RestServer<*> {
        val server = embeddedServer(Netty, config.port) { }
        server.start(wait = false)
        if (config.staticFiles != null) {
            registerStaticFiles(server, config.staticFiles)
        }
        return KtorRestServer(server)
    }

    private fun registerStaticFiles (server: ApplicationEngine, staticFiles: StaticFiles) {
        val indexHtml = Files.readAllBytes(Paths.get(staticFiles.indexPage))
        server.environment.application.routing {
            get("/*") {
                val filename = call.request.uri
                val path = Paths.get(staticFiles.staticFilesPath, filename)
                //TODO Check that it's actually a subpath and we don't just serve any files?
                if (Files.exists(path)) {
                    val bytes = Files.readAllBytes(path)
                    // TODO Decipher from file ending? I.e. CSS / JS
                    call.respondBytes(bytes, ContentType.Any)
                } else {
                    call.respondBytes(indexHtml, ContentType.Text.Html)
                }
            }
        }
    }
}