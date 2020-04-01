package ch.cyril.budget.manager.backend.util.gson

import ch.cyril.budget.manager.backend.model.Amount
import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class NullSafeTest {

    internal class Nullable(val optional: Int?)
    internal class Private(private val private: String)

    private val gson = GsonBuilder()
            .registerTypeAdapterFactory(NullSafeTypeAdapterFactory())
            .create()

    @Test
    fun notNullable() {
        var json = "{}"
        val exc = assertThrows<JsonParseException>{
            gson.fromJson(json, Amount::class.java)
        }

        Assertions.assertEquals("Property 'amount' of type 'Amount' is not nullable", exc.message)

        json = "{\"amount\":10}"
        val value = gson.fromJson(json, Amount::class.java)

        Assertions.assertEquals(10, value.amount.toInt())
    }

    @Test
    fun javaNullable() {
        var json = "{}"
        var value = gson.fromJson(json, JavaNullable::class.java)

        Assertions.assertNull(value.value)

        json = "{\"value\":10}"
        value = gson.fromJson(json, JavaNullable::class.java)

        Assertions.assertEquals(10, value.value)
    }

    @Test
    fun private() {
        val json = "{}"
        val exc = assertThrows<JsonParseException> {
            gson.fromJson(json, Private::class.java)
        }

        Assertions.assertEquals("Property 'private' of type 'Private' is not nullable", exc.message)
    }

    @Test
    fun nullable() {
        var json = "{}"
        var value = gson.fromJson(json, Nullable::class.java)

        Assertions.assertNull(value.optional)

        json = "{\"optional\":10}"
        value = gson.fromJson(json, Nullable::class.java)

        Assertions.assertEquals(10, value.optional)
    }
}