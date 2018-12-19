package ch.cyril.budget.manager.backend.systemtest

import ch.cyril.budget.manager.backend.main.ServerType
import java.nio.file.Path

object ParamBuilder {
    fun fileBased (expenseFile: Path, budgetFile: Path, server: ServerType, port: Int): String {
        return """
            {
                "type": "file",
                "params": {
                    "expenses": "${expenseFile.toString().replace("\\", "\\\\")}",
                    "budget": "${budgetFile.toString().replace("\\", "\\\\")}"
                },
                "server": "$server",
                "serverConfig": {
                    "port": $port
                }
            }
        """.trimIndent()
    }
}