package ch.cyril.budget.manager.backend.rest

import ch.cyril.budget.manager.backend.model.BudgetPeriod
import ch.cyril.budget.manager.backend.model.Id
import ch.cyril.budget.manager.backend.service.expense.SimpleExpenseQueryDescriptor
import ch.cyril.budget.manager.backend.service.expense.SortDirection
import ch.cyril.budget.manager.backend.service.expense.ExpenseSortField
import ch.cyril.budget.manager.backend.util.Identifiable
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.time.LocalDate

val GSON = GsonBuilder()
        .registerTypeAdapter(Id::class.java, IdTypeAdapter().nullSafe())
        .registerTypeAdapter(LocalDate::class.java, LocalDateTypeAdapter().nullSafe())
        .registerTypeAdapter(SimpleExpenseQueryDescriptor::class.java, SimpleQueryDescriptorAdapter().nullSafe())
        .registerTypeAdapter(ExpenseSortField::class.java, ExpenseSortFieldAdapter().nullSafe())
        .registerTypeAdapter(SortDirection::class.java, SortDirectionAdapter().nullSafe())
        .registerTypeAdapter(BudgetPeriod::class.java, BudgetPeriodAdapter().nullSafe())
        .create()

private class IdTypeAdapter : TypeAdapter<Id>() {
    override fun read(`in`: JsonReader): Id {
        return Id(`in`.nextInt())
    }

    override fun write(out: JsonWriter, value: Id) {
        out.value(value.id)
    }
}

private class LocalDateTypeAdapter : TypeAdapter<LocalDate>() {
    override fun read(`in`: JsonReader): LocalDate {
        return LocalDate.parse(`in`.nextString())
    }

    override fun write(out: JsonWriter, value: LocalDate) {
        out.value(value.toString())
    }
}

private class SimpleQueryDescriptorAdapter : TypeAdapter<SimpleExpenseQueryDescriptor>() {
    override fun read(`in`: JsonReader): SimpleExpenseQueryDescriptor {
        return Identifiable.byIdentifier(`in`.nextString())
    }

    override fun write(out: JsonWriter, value: SimpleExpenseQueryDescriptor) {
        out.value(value.identifier)
    }
}

private class SortDirectionAdapter : TypeAdapter<SortDirection>() {
    override fun read(`in`: JsonReader): SortDirection {
        return Identifiable.byIdentifier(`in`.nextString())
    }

    override fun write(out: JsonWriter, value: SortDirection) {
        out.value(value.identifier)
    }
}

private class ExpenseSortFieldAdapter : TypeAdapter<ExpenseSortField>() {
    override fun read(`in`: JsonReader): ExpenseSortField {
        return Identifiable.byIdentifier(`in`.nextString())
    }

    override fun write(out: JsonWriter, value: ExpenseSortField) {
        out.value(value.identifier)
    }
}

private class BudgetPeriodAdapter : TypeAdapter<BudgetPeriod>() {
    override fun read(`in`: JsonReader): BudgetPeriod {
        return Identifiable.byIdentifier(`in`.nextString())
    }

    override fun write(out: JsonWriter, value: BudgetPeriod) {
        out.value(value.identifier)
    }
}