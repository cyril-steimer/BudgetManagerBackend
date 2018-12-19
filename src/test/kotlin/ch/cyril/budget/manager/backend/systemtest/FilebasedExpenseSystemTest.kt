package ch.cyril.budget.manager.backend.systemtest

import ch.cyril.budget.manager.backend.main.ServerType
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.nio.file.Files

class FilebasedExpenseSystemTest {

    @ParameterizedTest
    @EnumSource(ServerType::class)
    fun test(type: ServerType) {
        val tempDir = Files.createTempDirectory(type.name + "-expenses")
        FilebasedExpenseSystemTester(tempDir, type, FilebasedBudgetSystemTest.port++).use {
            it.runExpenseSystemTests()
        }
    }
}