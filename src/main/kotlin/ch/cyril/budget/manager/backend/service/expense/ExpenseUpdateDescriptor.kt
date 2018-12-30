package ch.cyril.budget.manager.backend.service.expense

import ch.cyril.budget.manager.backend.model.Author
import ch.cyril.budget.manager.backend.util.Identifiable
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive

enum class ExpenseUpdateDescriptor(override val identifier: String) : Identifiable {

    AUTHOR("author") {
        override fun doCreateUpdate(value: JsonPrimitive): ExpenseUpdate {
            return AuthorExpenseUpdate(Author(value.asString))
        }
    };

    companion object {
        fun createUpdate(json: JsonObject): ExpenseUpdate {
            val key = json.keySet().single()
            val descriptor = Identifiable.byIdentifier<ExpenseUpdateDescriptor>(key)
            return descriptor.doCreateUpdate(json[key].asJsonPrimitive)
        }
    }

    protected abstract fun doCreateUpdate(value: JsonPrimitive): ExpenseUpdate
}
