package ch.cyril.budget.manager.backend.util.gson

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

abstract class NullHandlingTypeAdapter<T> : TypeAdapter<T>() {

    override fun write(out: JsonWriter, value: T?) {
        if (value == null) {
            out.nullValue()
        } else {
            doWrite(out, value)
        }
    }

    override fun read(`in`: JsonReader): T? {
        if (`in`.peek() == JsonToken.NULL) {
            `in`.nextNull()
            return null
        }
        return doRead(`in`)
    }

    protected abstract fun doWrite(out: JsonWriter, value: T)

    protected abstract fun doRead(`in`: JsonReader): T
}