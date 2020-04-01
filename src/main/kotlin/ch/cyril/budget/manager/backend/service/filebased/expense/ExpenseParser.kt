package ch.cyril.budget.manager.backend.service.filebased.expense

import ch.cyril.budget.manager.backend.model.*
import ch.cyril.budget.manager.backend.rest.GSON_BUILDER
import ch.cyril.budget.manager.backend.service.budget.BudgetDao
import ch.cyril.budget.manager.backend.service.filebased.JsonBasedFileParser
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

class ActualExpenseParser(budgetDao: BudgetDao) : JsonBasedFileParser<ActualExpense>(ActualExpense::class.java, gson(budgetDao))

class ExpenseTemplateParser(budgetDao: BudgetDao) : JsonBasedFileParser<ExpenseTemplate>(ExpenseTemplate::class.java, gson(budgetDao))

class ScheduledExpenseParser(budgetDao: BudgetDao): JsonBasedFileParser<ScheduledExpense>(ScheduledExpense::class.java, gson(budgetDao))

private class BudgetByIdTypeAdapter(
        private val budgetDao: BudgetDao,
        private val idAdapter: TypeAdapter<Id>) : TypeAdapter<Budget>() {

    override fun write(out: JsonWriter, value: Budget) {
        idAdapter.write(out, value.id)
    }

    override fun read(`in`: JsonReader): Budget? {
        val id: Id? = idAdapter.read(`in`)
        return id?.let { budgetDao.getOneBudget(it) }
    }
}

private class BudgetByIdTypeAdapterFactory(private val budgetDao: BudgetDao) : TypeAdapterFactory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any?> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
        if (type.type == Budget::class.java) {
            return BudgetByIdTypeAdapter(budgetDao, gson.getAdapter(Id::class.java)).nullSafe() as TypeAdapter<T>
        }
        return null
    }
}
private fun gson(budgetDao: BudgetDao): Gson {
    return GSON_BUILDER
            .registerTypeAdapterFactory(BudgetByIdTypeAdapterFactory(budgetDao))
            .create()
}