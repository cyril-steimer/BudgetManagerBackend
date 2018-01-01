package ch.cyril.budget.manager.backend.service.expense

import ch.cyril.budget.manager.backend.model.Id
import ch.cyril.budget.manager.backend.model.Name
import ch.cyril.budget.manager.backend.model.Category

enum class SimpleExpenseQueryDescriptor(
        val queryName: String,
        private val factory: (String) -> ExpenseQuery) {

    ID("id", { v -> IdExpenseQuery(Id(v.toInt())) } ),
    NAME("name", { v -> NameExpenseQuery(Name(v)) } ),
    CATEGORY("category", { v -> CategoryExpenseQuery(Category(v)) } );


    fun createQuery(value: String): ExpenseQuery {
        return factory.invoke(value)
    }

    companion object {
        fun byQueryName(name: String): SimpleExpenseQueryDescriptor {
            val res =  values()
                    .find { d -> d.queryName.equals(name) }
            return res ?: throw IllegalArgumentException("No query '$name'")
        }
    }
}