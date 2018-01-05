package ch.cyril.budget.manager.backend.service.filebased

import java.nio.file.Files
import java.nio.file.Path

abstract class LineBasedFileParser<T> {

    fun load(file: Path): List<T> {
        val lines = Files.readAllLines(file)
        return lines
                .map { l -> fromLine(l) }
    }

    fun store(file: Path, values: List<T>) {
        val lines = values
                .map { t -> toLine(t) }
        Files.write(file, lines)
    }

    protected abstract fun toLine(t: T): String

    protected abstract fun fromLine(line: String): T
}