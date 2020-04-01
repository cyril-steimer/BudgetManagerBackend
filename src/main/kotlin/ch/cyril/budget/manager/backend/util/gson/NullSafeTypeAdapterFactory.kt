package ch.cyril.budget.manager.backend.util.gson

import com.google.gson.JsonParseException
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

class NullSafeTypeAdapter<T>(delegate: TypeAdapter<T>) : DelegatingTypeAdapter<T>(delegate) {

    override fun read(`in`: JsonReader): T {
        val res = super.read(`in`)
        if (res != null) {
            checkAllFieldsInitialized(res)
        }
        return res
    }

    private fun checkAllFieldsInitialized(res: Any) {
        checkAllFieldsInitialized(res, res::class.java)
    }

    private fun isKotlinClass(cls: Class<*>): Boolean {
        // https://stackoverflow.com/a/39806722
        return cls.declaredAnnotations.any {
            it.annotationClass.qualifiedName == "kotlin.Metadata"
        }
    }

    private fun checkAllFieldsInitialized (res: Any, cls: Class<*>) {
        if (isKotlinClass(cls)) {
            val kotlin = cls.kotlin // The code below does not work for Java classes!
            for (property in kotlin.memberProperties) {
                if (!property.getter.returnType.isMarkedNullable) {
                    property.getter.isAccessible = true
                    val value = property.getter.call(res)
                    if (value == null) {
                        throw JsonParseException("Property '${property.name}' of type '${cls.simpleName}' is not nullable")
                    }
                }
            }
        }
    }
}

class NullSafeTypeAdapterFactory : DelegatingTypeAdapterFactory() {

    override fun <T> create(delegate: TypeAdapter<T>, type: TypeToken<T>): TypeAdapter<T> {
        return NullSafeTypeAdapter(delegate)
    }
}