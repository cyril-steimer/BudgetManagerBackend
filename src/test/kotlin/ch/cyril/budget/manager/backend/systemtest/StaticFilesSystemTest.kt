package ch.cyril.budget.manager.backend.systemtest

import ch.cyril.budget.manager.backend.main.ServerType
import ch.cyril.budget.manager.backend.main.StaticFiles
import ch.cyril.budget.manager.backend.main.startServer
import io.ktor.client.HttpClient
import io.ktor.client.call.call
import io.ktor.client.response.readText
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.nio.file.Files

class StaticFilesSystemTest {

    companion object {
        const val HTML = "<html></html>"
        const val JAVASCRIPT = "alert('Some Javascript');"
        const val CSS = ".element { }"
        const val PORT = 9000
    }

    @ParameterizedTest
    @EnumSource(ServerType::class)
    fun test(type: ServerType) {
        val distDir = Files.createTempDirectory("dist")
        val indexHtml = Files.write(distDir.resolve("index.html"), HTML.toByteArray())
        Files.write(distDir.resolve("bundle.js"), JAVASCRIPT.toByteArray())
        val cssDir = Files.createDirectories(distDir.resolve("assets").resolve("css"))
        Files.write(cssDir.resolve("style.css"), CSS.toByteArray())
        val staticFiles = StaticFiles(distDir.toString(), indexHtml.toString())

        val config = ParamBuilder.withStaticFiles(type, staticFiles, PORT)
        val configFile = Files.write(distDir.resolve("config.json"), config.toByteArray())
        val server = startServer(arrayOf(configFile.toString()))

        server.use {
            checkContentAndContentType("", HTML, ContentType.Text.Html)
            checkContentAndContentType("/index.html", HTML, ContentType.Text.Html)
            checkContentAndContentType("/bundle.js", JAVASCRIPT, ContentType.Application.JavaScript)
            checkContentAndContentType("/assets/css/style.css", CSS, ContentType.Text.CSS)
        }
    }

    private fun checkContentAndContentType (path: String, expectedContent: String, expectedContentType: ContentType) {
        val client = HttpClient()
        runBlocking {
            val call = client.call("http://127.0.0.1:$PORT$path") {
                method = HttpMethod.Get
            }
            assertEquals(expectedContentType, call.response.contentType())
            assertEquals(expectedContent, call.response.readText())
        }
    }
}