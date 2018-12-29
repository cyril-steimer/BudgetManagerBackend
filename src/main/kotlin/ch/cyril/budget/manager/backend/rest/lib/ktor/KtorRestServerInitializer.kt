package ch.cyril.budget.manager.backend.rest.lib.ktor

import ch.cyril.budget.manager.backend.main.ServerConfig
import ch.cyril.budget.manager.backend.main.StaticFiles
import ch.cyril.budget.manager.backend.rest.lib.RestServer
import ch.cyril.budget.manager.backend.rest.lib.RestServerInitializer
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.fromFilePath
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
            get("/{path...}") {
                //TODO Check that it's actually a subpath and we don't just serve any files?
                val filename = call.parameters.getAll("path")!!.joinToString("/")
                val path = Paths.get(staticFiles.staticFilesPath, filename)
                if (Files.exists(path)) {
                    val bytes = Files.readAllBytes(path)
                    val types = ContentType.fromFilePath(path.toString())
                    val type = if (types.isNotEmpty()) types[0] else ContentType.Any
                    call.respondBytes(bytes, type)
                } else {
                    call.respondBytes(indexHtml, ContentType.Text.Html)
                }
            }

            get("") {
                call.respondBytes(indexHtml, ContentType.Text.Html)
            }
        }
    }
}