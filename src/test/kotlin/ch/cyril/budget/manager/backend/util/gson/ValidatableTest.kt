package ch.cyril.budget.manager.backend.util.gson

import ch.cyril.budget.manager.backend.model.Amount
import ch.cyril.budget.manager.backend.model.Author
import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ValidatableTest {

    private val gson = GsonBuilder()
            .registerTypeAdapterFactory(ValidatingTypeAdapterFactory())
            .create()

    @Test
    fun notValidated() {
        val json = "{\"name\":\"Some name\"}"
        val value = gson.fromJson(json, Author::class.java)

        Assertions.assertEquals("Some name", value.name)
    }

    @Test
    fun valid() {
        val json = "{\"amount\":10}"
        val value = gson.fromJson(json, Amount::class.java)

        Assertions.assertEquals(10, value.amount.toInt())
    }

    @Test
    fun invalid() {
        val json = "{\"amount\":-10}"
        val exc = assertThrows<JsonParseException> {
            gson.fromJson(json, Amount::class.java)
        }

        Assertions.assertEquals("Amount must be >= 0, was -10", exc.message)
    }
}