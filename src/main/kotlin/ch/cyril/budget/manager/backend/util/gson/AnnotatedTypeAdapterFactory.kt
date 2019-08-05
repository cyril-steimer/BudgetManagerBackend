package ch.cyril.budget.manager.backend.util.gson

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

annotation class Serializer(val cls: KClass<out TypeAdapter<*>>)

class AnnotatedTypeAdapterFactory : TypeAdapterFactory {

    override fun <T : Any?> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
        val annotation: Serializer? = type.rawType.getAnnotation(Serializer::class.java)
        if (annotation != null) {
            @Suppress("UNCHECKED_CAST")
            return annotation.cls.createInstance() as TypeAdapter<T>
        }
        return null
    }
}