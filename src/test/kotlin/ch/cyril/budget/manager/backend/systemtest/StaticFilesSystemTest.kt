package ch.cyril.budget.manager.backend.systemtest

import ch.cyril.budget.manager.backend.main.ServerType
import ch.cyril.budget.manager.backend.main.StaticFiles
import ch.cyril.budget.manager.backend.main.startServer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.nio.file.Files

class StaticFilesSystemTest {

    companion object {
        const val HTML = "<html></html>"
        const val JAVASCRIPT = "alert('Some Javascript');"
    }

    private val client = HttpClient(9000)

    @ParameterizedTest
    @EnumSource(ServerType::class)
    fun test(type: ServerType) {
        val tempDir = Files.createTempDirectory("test")
        val indexHtml = Files.write(tempDir.resolve("index.html"), HTML.toByteArray())
        val javascriptDir = Files.createDirectory(tempDir.resolve("dist"))
        Files.write(javascriptDir.resolve("bundle.js"), JAVASCRIPT.toByteArray())
        val staticFiles = StaticFiles(javascriptDir.toString(), indexHtml.toString())

        val config = ParamBuilder.withStaticFiles(type, staticFiles, 9000)
        val configFile = Files.write(tempDir.resolve("config.json"), config.toByteArray())
        val server = startServer(arrayOf(configFile.toString()))

        server.use {
            assertEquals(HTML, client.getString("/index.html"))
            assertEquals(JAVASCRIPT, client.getString("/bundle.js"))
        }
    }
}