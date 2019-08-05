package ch.cyril.budget.manager.backend.util.gson

import com.google.gson.JsonParseException
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader

interface Validatable {

    @Throws(JsonParseException::class)
    fun validate()
}

class ValidatingTypeAdapterFactory : DelegatingTypeAdapterFactory() {

    override fun <T> create(delegate: TypeAdapter<T>, type: TypeToken<T>): TypeAdapter<T> {
        return ValidatingTypeAdapter(delegate)
    }
}

class ValidatingTypeAdapter<T>(delegate: TypeAdapter<T>) : DelegatingTypeAdapter<T>(delegate) {

    override fun read(`in`: JsonReader): T {
        val res = super.read(`in`)
        if (res is Validatable) {
            res.validate()
        }
        return res
    }
}