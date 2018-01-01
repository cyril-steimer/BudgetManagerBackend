package ch.cyril.budget.manager.backend.rest

import ch.cyril.budget.manager.backend.model.Category
import ch.cyril.budget.manager.backend.model.Id
import ch.cyril.budget.manager.backend.model.Name
import ch.cyril.budget.manager.backend.service.expense.SimpleExpenseQueryDescriptor
import ch.cyril.budget.manager.backend.service.SortDirection
import ch.cyril.budget.manager.backend.service.expense.ExpenseSortField
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.time.LocalDate

val GSON = GsonBuilder()
        .registerTypeAdapter(Id::class.java, IdTypeAdapter().nullSafe())
        .registerTypeAdapter(Name::class.java, NameTypeAdapter().nullSafe())
        .registerTypeAdapter(Category::class.java, CategoryTypeAdapter().nullSafe())
        .registerTypeAdapter(LocalDate::class.java, LocalDateTypeAdapter().nullSafe())
        .registerTypeAdapter(SimpleExpenseQueryDescriptor::class.java, SimpleQueryDescriptorAdapter().nullSafe())
        .registerTypeAdapter(ExpenseSortField::class.java, ExpenseSortFieldAdapter().nullSafe())
        .registerTypeAdapter(SortDirection::class.java, SortDirectionAdapter().nullSafe())
        .create()

private class IdTypeAdapter : TypeAdapter<Id>() {
    override fun read(`in`: JsonReader): Id {
        return Id(`in`.nextInt())
    }

    override fun write(out: JsonWriter, value: Id) {
        out.value(value.id)
    }
}

private class NameTypeAdapter : TypeAdapter<Name>() {
    override fun read(`in`: JsonReader): Name {
        return Name(`in`.nextString())
    }

    override fun write(out: JsonWriter, value: Name) {
        out.value(value.name)
    }
}

private class CategoryTypeAdapter : TypeAdapter<Category>() {
    override fun read(`in`: JsonReader): Category {
        return Category(`in`.nextString())
    }

    override fun write(out: JsonWriter, value: Category) {
        out.value(value.name)
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
        return SimpleExpenseQueryDescriptor.byQueryName(`in`.nextString())
    }

    override fun write(out: JsonWriter, value: SimpleExpenseQueryDescriptor) {
        out.value(value.queryName)
    }
}

private class SortDirectionAdapter : TypeAdapter<SortDirection>() {
    override fun read(`in`: JsonReader): SortDirection {
        return SortDirection.byDirection(`in`.nextString())
    }

    override fun write(out: JsonWriter, value: SortDirection) {
        out.value(value.direction)
    }
}

private class ExpenseSortFieldAdapter : TypeAdapter<ExpenseSortField>() {
    override fun read(`in`: JsonReader): ExpenseSortField {
        return ExpenseSortField.byFieldName(`in`.nextString())
    }

    override fun write(out: JsonWriter, value: ExpenseSortField) {
        out.value(value.fieldName)
    }

}