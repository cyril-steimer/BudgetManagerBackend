package ch.cyril.budget.manager.backend.service.filebased

import ch.cyril.budget.manager.backend.rest.GSON
import java.lang.reflect.Array
import java.nio.file.Files
import java.nio.file.Path

abstract class JsonBasedFileParser<T>(private val cls: Class<T>) : FileParser<T> {

    override fun read(file: Path): List<T> {
        if (!Files.exists(file)) {
            return emptyList()
        }
        val type = Array.newInstance(cls, 0).javaClass
        Files.newBufferedReader(file, Charsets.UTF_8).use {
            val array = GSON.fromJson<kotlin.Array<T>>(Files.newBufferedReader(file, Charsets.UTF_8), type)
            return array.toList()
        }
    }

    override fun write(file: Path, values: List<T>) {
        Files.newBufferedWriter(file, Charsets.UTF_8).use {
            GSON.toJson(values, it)
        }
    }
}