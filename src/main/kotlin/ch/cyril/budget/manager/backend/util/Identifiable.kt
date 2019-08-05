package ch.cyril.budget.manager.backend.util

import ch.cyril.budget.manager.backend.util.gson.NullHandlingTypeAdapter
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import kotlin.reflect.KClass

interface Identifiable {
    val identifier: String

    companion object {
        inline fun <reified T> byIdentifier(identifier: String): T
                where T : Enum<T>, T : Identifiable {

            return Companion.byIdentifier(identifier, T::class.java)
        }

        fun <T> byIdentifier(identifier: String, cls: Class<T>): T
                where T : Enum<T>, T : Identifiable {

            val values = cls.enumConstants
            for (value in values) {
                if ((value as Identifiable).identifier == identifier) {
                    return value
                }
            }
            throw IllegalArgumentException("No enum constant with identifier '$identifier'")
        }
    }
}

open class IdentifiableTypeAdapter<T>(private val cls: KClass<T>) : NullHandlingTypeAdapter<T>()
    where T : Enum<T>, T : Identifiable {

    override fun doWrite(out: JsonWriter, value: T) {
        out.value(value.identifier)
    }

    override fun doRead(`in`: JsonReader): T {
        return Identifiable.byIdentifier(`in`.nextString(), cls.java)
    }
}