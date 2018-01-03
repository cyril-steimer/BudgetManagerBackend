package ch.cyril.budget.manager.backend.service.expense

import ch.cyril.budget.manager.backend.model.Id
import ch.cyril.budget.manager.backend.model.Name
import ch.cyril.budget.manager.backend.model.Category
import ch.cyril.budget.manager.backend.model.Amount
import ch.cyril.budget.manager.backend.util.Identifiable
import ch.cyril.budget.manager.backend.service.StringComparison
import ch.cyril.budget.manager.backend.service.MathComparison
import ch.cyril.budget.manager.backend.service.StringCase
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import java.math.BigDecimal
import java.time.LocalDate

interface ExpenseQueryDescriptor : Identifiable {

    fun createQuery(value: JsonElement): ExpenseQuery

    companion object {
        fun getQueryDescriptor(identifier: String): ExpenseQueryDescriptor {
            try {
                return Identifiable.byIdentifier<SimpleExpenseQueryDescriptor>(identifier)
            } catch (e: Exception) {
                return Identifiable.byIdentifier<CompositeExpenseQueryDescriptor>(identifier)
            }
        }

        fun createQuery(json: JsonObject): ExpenseQuery {
            val key = json.keySet().single()
            val descriptor = getQueryDescriptor(key)
            return descriptor.createQuery(json[key])
        }
    }
}

enum class SimpleExpenseQueryDescriptor(override val identifier: String)
    : Identifiable, ExpenseQueryDescriptor {

    ID("id") {
        override fun createQuery(value: JsonPrimitive): ExpenseQuery {
            val id = getBigDecimal(value)
            return IdExpenseQuery(Id(id.toInt()))
        }

        override fun createQuery(value: JsonObject): ExpenseQuery {
            throw IllegalStateException("Id query is never complex")
        }
    },
    NAME("name") {
        override fun createQuery(value: JsonPrimitive): ExpenseQuery {
            return NameExpenseQuery(Name(value.asString), StringComparison.CONTAINS, StringCase.CASE_INSENSITIVE)
        }

        override fun createQuery(value: JsonObject): ExpenseQuery {
            val name = value.get("name").asString
            val comparison = Identifiable.byIdentifier<StringComparison>(value.get("comparison").asString)
            val case = Identifiable.byIdentifier<StringCase>(value.get("case").asString)
            return NameExpenseQuery(Name(name), comparison, case)
        }
    },
    CATEGORY("category") {
        override fun createQuery(value: JsonPrimitive): ExpenseQuery {
            return CategoryExpenseQuery(Category(value.asString), StringComparison.CONTAINS, StringCase.CASE_INSENSITIVE)
        }

        override fun createQuery(value: JsonObject): ExpenseQuery {
            val category = value.get("category").asString
            val comparison = Identifiable.byIdentifier<StringComparison>(value.get("comparison").asString)
            val case = Identifiable.byIdentifier<StringCase>(value.get("case").asString)
            return CategoryExpenseQuery(Category(category), comparison, case)
        }
    },
    AMOUNT("amount") {
        override fun createQuery(value: JsonPrimitive): ExpenseQuery {
            val amount = getBigDecimal(value)
            return AmountExpenseQuery(Amount(amount), MathComparison.EQ)
        }

        override fun createQuery(value: JsonObject): ExpenseQuery {
            val amount = value.get("amount").asBigDecimal
            val comparison = Identifiable.byIdentifier<MathComparison>(value.get("comparison").asString)
            return AmountExpenseQuery(Amount(amount), comparison)
        }
    },
    DATE("date") {
        override fun createQuery(value: JsonPrimitive): ExpenseQuery {
            return DateExpenseQuery(LocalDate.parse(value.asString), MathComparison.EQ)
        }

        override fun createQuery(value: JsonObject): ExpenseQuery {
            val date = LocalDate.parse(value.get("date").asString)
            val comparison = Identifiable.byIdentifier<MathComparison>(value.get("comparison").asString)
            return DateExpenseQuery(date, comparison)
        }
    };

    override fun createQuery(value: JsonElement): ExpenseQuery {
        if (value.isJsonPrimitive) {
            return createQuery(value.asJsonPrimitive)
        }
        return createQuery(value.asJsonObject)
    }

    protected abstract fun createQuery(value: JsonPrimitive): ExpenseQuery

    protected abstract fun createQuery(value: JsonObject): ExpenseQuery

    protected fun getBigDecimal(value: JsonPrimitive): BigDecimal {
        if (value.isNumber) {
            return value.asBigDecimal
        }
        return value.asString.toBigDecimal()
    }
}

enum class CompositeExpenseQueryDescriptor
    (override val identifier: String, private val factory: (List<ExpenseQuery>) -> ExpenseQuery)
    : Identifiable, ExpenseQueryDescriptor {

    OR("or", ::OrExpenseQuery),
    AND("and", ::AndExpenseQuery);

    override fun createQuery(value: JsonElement): ExpenseQuery {
        val queries = value.asJsonArray
                .map { o -> ExpenseQueryDescriptor.createQuery(o.asJsonObject) }
        return factory.invoke(queries)
    }
}