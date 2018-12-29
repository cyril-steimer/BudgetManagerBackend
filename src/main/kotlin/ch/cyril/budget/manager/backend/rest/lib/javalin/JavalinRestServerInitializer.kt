package ch.cyril.budget.manager.backend.rest.lib.javalin

import ch.cyril.budget.manager.backend.main.ServerConfig
import ch.cyril.budget.manager.backend.rest.lib.RestServer
import ch.cyril.budget.manager.backend.rest.lib.RestServerInitializer
import io.javalin.Context
import io.javalin.Javalin
import io.javalin.staticfiles.Location
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths

class JavalinRestServerInitializer : RestServerInitializer() {

    override fun doStartServer(config: ServerConfig): RestServer<*> {
        val javalin = Javalin.create().port(config.port)
        if (config.staticFiles != null) {
            javalin.enableStaticFiles(config.staticFiles.staticFilesPath, Location.EXTERNAL)
            val html = String(Files.readAllBytes(Paths.get(config.staticFiles.indexPage)), StandardCharsets.UTF_8)
            javalin.after { ctx -> enableSinglePageMode(ctx, html) }
        }
        return JavalinRestServer(javalin.start())
    }

    private fun enableSinglePageMode(ctx: Context, html: String) {
        if (ctx.res.status == 404) {
            // This ensures that whenever we would actually return a '404', we instead return 'index.html'.
            // Angular can then work its magic to show the correct page within its application.
            ctx.res.reset()
            ctx.html(html)
        }
    }
}