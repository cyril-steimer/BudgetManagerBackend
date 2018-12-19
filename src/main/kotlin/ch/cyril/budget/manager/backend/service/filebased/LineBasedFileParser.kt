package ch.cyril.budget.manager.backend.service.filebased

import java.nio.file.Files
import java.nio.file.Path

abstract class LineBasedFileParser<T> : FileParser<T> {

    override fun read(file: Path): List<T> {
        if (!Files.exists(file)) {
            return emptyList()
        }
        val lines = Files.readAllLines(file)
        return lines
                .map { l -> fromLine(l) }
    }

    override fun write(file: Path, values: List<T>) {
        val lines = values
                .map { t -> toLine(t) }
        Files.write(file, lines)
    }

    protected abstract fun toLine(t: T): String

    protected abstract fun fromLine(line: String): T
}