package ch.cyril.budget.manager.backend.systemtest

import ch.cyril.budget.manager.backend.main.ServerType
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.nio.file.Files


class FilebasedBudgetSystemTest {

    @ParameterizedTest
    @EnumSource(ServerType::class)
    fun test(type: ServerType) {
        val tempDir = Files.createTempDirectory(type.name + "-budgets")
        FilebasedBudgetSystemTester(tempDir, type, 9000).use {
            it.runBudgetSystemTests()
        }
    }
}