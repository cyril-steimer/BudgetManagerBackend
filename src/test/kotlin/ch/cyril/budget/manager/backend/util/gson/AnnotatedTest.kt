package ch.cyril.budget.manager.backend.util.gson

import ch.cyril.budget.manager.backend.model.Id
import ch.cyril.budget.manager.backend.service.expense.SortDirection
import com.google.gson.GsonBuilder
import com.google.gson.JsonPrimitive
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class AnnotatedTest {

    private val gson = GsonBuilder()
            .registerTypeAdapterFactory(AnnotatedTypeAdapterFactory())
            .create()

    @Test
    fun serializeId() {
        val id = Id("10")
        val actual = gson.toJsonTree(id)

        val expected = JsonPrimitive("10")
        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun deserializeId() {
        val actual = gson.fromJson(JsonPrimitive("20"), Id::class.java)

        val expected = Id("20")
        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun serializeSortDirection() {
        val actual = gson.toJsonTree(SortDirection.ASCENDING)

        val expected = JsonPrimitive("asc")
        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun deserializeSortDirection() {
        val actual = gson.fromJson(JsonPrimitive("desc"), SortDirection::class.java)

        val expected = SortDirection.DESCENDING
        Assertions.assertEquals(expected, actual)
    }
}