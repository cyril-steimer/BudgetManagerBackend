package ch.cyril.budget.manager.backend.util.gson

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.IOException

open class DelegatingTypeAdapter<T>(protected val delegate: TypeAdapter<T>) : TypeAdapter<T>() {

    @Throws(IOException::class)
    override fun read(`in`: JsonReader): T {
        return this.delegate.read(`in`)
    }

    @Throws(IOException::class)
    override fun write(`in`: JsonWriter, value: T) {
        this.delegate.write(`in`, value)
    }
}

abstract class DelegatingTypeAdapterFactory : TypeAdapterFactory {

    override fun <T> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T> {
        return create(gson.getDelegateAdapter(this, type), type)
    }


    protected abstract fun <T> create(delegate: TypeAdapter<T>, type: TypeToken<T>): TypeAdapter<T>
}