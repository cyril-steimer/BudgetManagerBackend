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
        cache.removeIf { v -> idGetter(v) == idGetter(value) }
        cache.add(value)
        persistCache()
    }

    fun update (value: T) {
        add(value)
    }

    fun delete (value: T) {
        val cache = doGetAll()
        cache.removeIf { v -> idGetter(v) == idGetter(value) }
        persistCache()
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