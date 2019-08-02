package ch.cyril.budget.manager.backend.systemtest

import ch.cyril.budget.manager.backend.main.ServerType
import ch.cyril.budget.manager.backend.main.StaticFiles
import java.nio.file.Files
import java.nio.file.Path

object ParamBuilder {
    fun fileBased (expenseFile: Path, templateFile: Path, budgetFile: Path, server: ServerType, port: Int): String {
        return """
            {
                "type": "file",
                "params": {
                    "expenses": "${expenseFile.toString().replace("\\", "\\\\")}",
                    "templates": "${templateFile.toString().replace("\\", "\\\\")}",
                    "budget": "${budgetFile.toString().replace("\\", "\\\\")}"
                },
                "server": "$server",
                "serverConfig": {
                    "port": $port
                }
            }
        """.trimIndent()
    }

    fun withStaticFiles (server:ServerType, staticFiles: StaticFiles, port: Int): String {
        val tempDir = Files.createTempDirectory("static-files")
        return """
            {
                "type": "file",
                "params": {
                    "expenses": "${tempDir.resolve("exp").toString().replace("\\", "\\\\")}",
                    "templates": "${tempDir.resolve("template").toString().replace("\\", "\\\\")}",
                    "budget": "${tempDir.resolve("bud").toString().replace("\\", "\\\\")}"
                },
                "server": "$server",
                "serverConfig": {
                    "port": $port,
                    "staticFiles": {
                        "indexPage": "${staticFiles.indexPage.replace("\\", "\\\\")}",
                        "staticFilesPath": "${staticFiles.staticFilesPath.replace("\\", "\\\\")}"
                    }
                }
            }
        """.trimIndent()
    }
}