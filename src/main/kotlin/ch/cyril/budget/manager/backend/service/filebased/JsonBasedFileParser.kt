package ch.cyril.budget.manager.backend.service.filebased

import com.google.gson.Gson
import java.lang.reflect.Array
import java.nio.file.Files
import java.nio.file.Path

abstract class JsonBasedFileParser<T>(private val cls: Class<T>, private val gson: Gson) : FileParser<T> {

    override fun read(file: Path): List<T> {
        if (!Files.exists(file)) {
            return emptyList()
        }
        val type = Array.newInstance(cls, 0).javaClass
        Files.newBufferedReader(file, Charsets.UTF_8).use {
            val array = gson.fromJson<kotlin.Array<T>>(Files.newBufferedReader(file, Charsets.UTF_8), type)
            return array.toList()
        }
    }

    override fun write(file: Path, values: List<T>) {
        Files.newBufferedWriter(file, Charsets.UTF_8).use {
            gson.toJson(values, it)
        }
    }
}