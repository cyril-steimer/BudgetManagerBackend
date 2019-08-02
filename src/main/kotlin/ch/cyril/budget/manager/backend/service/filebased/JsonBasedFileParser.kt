package ch.cyril.budget.manager.backend.service.filebased

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Array
import java.lang.reflect.Type
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

abstract class JsonBasedFileParser<T>(
        private val cls: Class<T>,
        private val gson: Gson) : FileParser<T> {

    override fun read(file: Path): List<T> {
        if (!Files.exists(file)) {
            return emptyList()
        }
        val type = Array.newInstance(cls, 0).javaClass
        val array = gson.fromJson(Files.newBufferedReader(file, Charsets.UTF_8), type) as kotlin.Array<T>
        return array.toList()
    }

    override fun write(file: Path, values: List<T>) {
        gson.toJson(values, Files.newBufferedWriter(file, Charsets.UTF_8))
    }
}