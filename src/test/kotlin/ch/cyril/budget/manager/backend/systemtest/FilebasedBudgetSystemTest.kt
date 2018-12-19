package ch.cyril.budget.manager.backend.systemtest

import ch.cyril.budget.manager.backend.main.ServerType
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.nio.file.Files


class FilebasedBudgetSystemTest {

    companion object {
        //TODO Remove this, once the tester properly cleans up after itself
        var port = 9000;
    }

    @ParameterizedTest
    @EnumSource(ServerType::class)
    fun test (type: ServerType) {
        FilebasedBudgetSystemTester(Files.createTempDirectory(type.name), type, port++).use {
            it.runBudgetSystemTests()
        }
    }
}