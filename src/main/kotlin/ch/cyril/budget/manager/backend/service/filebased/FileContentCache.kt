package ch.cyril.budget.manager.backend.service.filebased

import java.nio.file.Path

class FileContentCache<T, Id>(
        private val file: Path,
        private val parser: FileParser<T>,
        private val idGetter: (T) -> Id) {

    private var cache: MutableList<T>? = null;

    fun getAll (): List<T> {
        return doGetAll()
    }

    fun getById (id: Id): T? {
        return doGetAll().find { v -> idGetter(v) == id }
    }

    fun add (value: T) {
        val cache = doGetAll()
        if (cache.find { v -> idGetter(v) == idGetter(value) } != null) {
            throw IllegalStateException("There is already an object with id ${idGetter(value)}.")
        }
        cache.add(value)
        persistCache()
    }

    fun update (value: T) {
        val cache = doGetAll()
        val index = indexOf(idGetter(value), cache)
        cache[index] = value
        persistCache()
    }

    fun delete (id: Id) {
        val cache = doGetAll()
        val index = indexOf(id, cache)
        cache.removeAt(index)
        persistCache()
    }

    private fun indexOf (id: Id, values: List<T>): Int {
        val index = values.indexOfFirst { v -> idGetter(v) == id }
        if (index < 0) {
            throw IllegalStateException("There is no object with id ${id}")
        }
        return index
    }

    private fun persistCache () {
        parser.write(file, cache!!)
    }

    private fun doGetAll (): MutableList<T> {
        if (cache == null) {
            cache = ArrayList(parser.read(file))
        }
        return cache!!
    }
}

interface FileParser<T> {

    fun read(file: Path): List<T>

    fun write(file: Path, values: List<T>): Unit
}