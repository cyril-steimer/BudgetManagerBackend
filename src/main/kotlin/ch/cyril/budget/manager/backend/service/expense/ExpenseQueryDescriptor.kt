package ch.cyril.budget.manager.backend.service.expense

import ch.cyril.budget.manager.backend.model.Id
import ch.cyril.budget.manager.backend.model.Name
import ch.cyril.budget.manager.backend.model.Category
import ch.cyril.budget.manager.backend.util.Identifiable
import ch.cyril.budget.manager.backend.service.StringComparison
import ch.cyril.budget.manager.backend.service.StringCase

enum class SimpleExpenseQueryDescriptor(
        override val identifier: String,
        private val factory: (String) -> ExpenseQuery) : Identifiable {

    ID("id", { v -> IdExpenseQuery(Id(v.toInt())) } ),
    NAME("name", { v -> NameExpenseQuery(Name(v), StringComparison.CONTAINS, StringCase.CASE_INSENSITIVE) } ),
    CATEGORY("category", { v -> CategoryExpenseQuery(Category(v), StringComparison.CONTAINS, StringCase.CASE_INSENSITIVE) } );


    fun createQuery(value: String): ExpenseQuery {
        return factory.invoke(value)
    }
}