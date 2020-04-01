package ch.cyril.budget.manager.backend.model

import ch.cyril.budget.manager.backend.util.Identifiable
import ch.cyril.budget.manager.backend.util.IdentifiableTypeAdapter
import ch.cyril.budget.manager.backend.util.gson.NullHandlingTypeAdapter
import ch.cyril.budget.manager.backend.util.gson.Validatable
import com.google.gson.*
import com.google.gson.annotations.JsonAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.lang.reflect.Type
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class BudgetWithoutId(
        val category: Category,
        val amounts: List<BudgetAmount>) {

    fun withId(id: Id): Budget {
        return Budget(id, category, amounts)
    }
}

data class Budget(
        val id: Id,
        val category: Category,
        val amounts: List<BudgetAmount>) {

    fun withoutId(): BudgetWithoutId {
        return BudgetWithoutId(category, amounts)
    }
}

// TODO Don't allow overlapping budget amounts in all DAOs (or maybe the validate method?)

data class BudgetAmount(
        val amount: Amount,
        val period: BudgetPeriod,
        val from: MonthYear,
        val to: MonthYear) : Validatable {

    override fun validate() {
        if (from.toEpochMonth() > to.toEpochMonth()) {
            throw JsonParseException("From must be before to. From was '${from}', to was '${to}'")
        }
    }
}

data class MonthYear(val month: Int, val year: Int) : Validatable {

    override fun validate() {
        if (year < 0) {
            throw JsonParseException("Year must be >= 0, was ${year}")
        }
        if (month < 1 || month > 12) {
            throw JsonParseException("Month must be between 1 and 12, was ${month}")
        }
    }

    fun toEpochMonth(): Int {
        return (year * 12) + (month - 1)
    }

    companion object {
        fun fromEpochMonth(epochMonth: Int): MonthYear {
            val month = epochMonth % 12
            val year = epochMonth / 12
            return MonthYear(month + 1, year)
        }
    }
}

@JsonAdapter(BudgetPeriodTypeAdapter::class)
enum class BudgetPeriod(override val identifier: String) : Identifiable {
    YEARLY("yearly"),
    MONTHLY("monthly")
}

class BudgetPeriodTypeAdapter : IdentifiableTypeAdapter<BudgetPeriod>(BudgetPeriod::class)

data class MonthYearPeriod(val from: MonthYear, val to: MonthYear)

data class BudgetInPeriod(val budget: Budget, val amount: Amount)

data class Tag(val name: String)

data class PaymentMethod(val name: String)

data class Amount(val amount: BigDecimal) : Validatable {

    override fun validate() {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw JsonParseException("Amount must be >= 0, was ${amount}")
        }
    }
}

data class Author(val name: String)

@JsonAdapter(IdTypeAdapter::class)
data class Id(val id: String)

class IdTypeAdapter : NullHandlingTypeAdapter<Id>() {
    override fun doRead(`in`: JsonReader): Id {
        return Id(`in`.nextString())
    }

    override fun doWrite(out: JsonWriter, value: Id) {
        out.value(value.id)
    }
}

data class Name(val name: String)

data class Category(val name: String)

@JsonAdapter(TimestampAdapter::class)
class Timestamp internal constructor(internal val date: LocalDate) {

    companion object {
        private val FORMATTER = DateTimeFormatter.ISO_DATE

        fun parse (text: String): Timestamp {
            return Timestamp(LocalDate.from(FORMATTER.parse(text)))
        }

        fun ofEpochDay(day: Long): Timestamp {
            return Timestamp(LocalDate.ofEpochDay(day))
        }

        fun now(): Timestamp {
            return ofEpochDay(LocalDate.now().toEpochDay())
        }
    }

    override fun toString(): String {
        return FORMATTER.format(this.date)
    }

    fun getEpochDay(): Long {
        return this.date.toEpochDay()
    }

    override fun equals(other: Any?): Boolean {
        if (other is Timestamp) {
            return date == other.date
        }
        return false
    }

    override fun hashCode(): Int {
        return date.hashCode()
    }
}

class TimestampAdapter : JsonSerializer<Timestamp>, JsonDeserializer<Timestamp> {

    override fun serialize(src: Timestamp, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val res = JsonObject()
        res.addProperty("day", src.date.dayOfMonth)
        res.addProperty("month", src.date.monthValue)
        res.addProperty("year", src.date.year)
        return res
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Timestamp {
        val obj = json.asJsonObject
        if (obj.has("timestamp")) {
            // This is how the timestamp was serialized in previous versions.
            val instant = Instant.ofEpochMilli(obj.get("timestamp").asLong)
            val zoned = instant.atZone(ZoneId.of("UTC"))
            return Timestamp.ofEpochDay(zoned.toLocalDate().toEpochDay())
        }
        val dayOfMonth = obj.get("day").asInt
        val month = obj.get("month").asInt
        val year = obj.get("year").asInt
        return Timestamp(LocalDate.of(year, month, dayOfMonth))
    }
}
