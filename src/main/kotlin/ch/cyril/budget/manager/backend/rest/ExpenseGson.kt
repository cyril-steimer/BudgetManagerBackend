package ch.cyril.budget.manager.backend.rest

import ch.cyril.budget.manager.backend.model.BudgetPeriod
import ch.cyril.budget.manager.backend.model.Id
import ch.cyril.budget.manager.backend.service.expense.SimpleExpenseQueryDescriptor
import ch.cyril.budget.manager.backend.service.expense.SortDirection
import ch.cyril.budget.manager.backend.service.expense.ExpenseSortField
import ch.cyril.budget.manager.backend.util.Identifiable
import ch.cyril.budget.manager.backend.util.gson.AnnotatedTypeAdapterFactory
import ch.cyril.budget.manager.backend.util.gson.NullSafeTypeAdapterFactory
import ch.cyril.budget.manager.backend.util.gson.ValidatingTypeAdapterFactory
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.time.LocalDate

val GSON = GsonBuilder()
        .registerTypeAdapter(LocalDate::class.java, LocalDateTypeAdapter().nullSafe())
        .registerTypeAdapterFactory(AnnotatedTypeAdapterFactory())
        .registerTypeAdapterFactory(NullSafeTypeAdapterFactory())
        .registerTypeAdapterFactory(ValidatingTypeAdapterFactory())
        .create()

private class LocalDateTypeAdapter : TypeAdapter<LocalDate>() {
    override fun read(`in`: JsonReader): LocalDate {
        return LocalDate.parse(`in`.nextString())
    }

    override fun write(out: JsonWriter, value: LocalDate) {
        out.value(value.toString())
    }
}