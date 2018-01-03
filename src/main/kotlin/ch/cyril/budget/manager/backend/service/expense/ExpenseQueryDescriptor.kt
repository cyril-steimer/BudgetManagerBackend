package ch.cyril.budget.manager.backend.service.expense

import ch.cyril.budget.manager.backend.model.Id
import ch.cyril.budget.manager.backend.model.Name
import ch.cyril.budget.manager.backend.model.Category
import ch.cyril.budget.manager.backend.model.Amount
import ch.cyril.budget.manager.backend.util.Identifiable
import ch.cyril.budget.manager.backend.service.StringComparison
import ch.cyril.budget.manager.backend.service.MathComparison
import ch.cyril.budget.manager.backend.service.StringCase
import java.time.LocalDate

enum class SimpleExpenseQueryDescriptor(
        override val identifier: String,
        private val factory: (String) -> ExpenseQuery) : Identifiable {

    ID("id", { v -> IdExpenseQuery(Id(v.toInt())) } ),
    NAME("name", { v -> NameExpenseQuery(Name(v), StringComparison.CONTAINS, StringCase.CASE_INSENSITIVE) } ),
    CATEGORY("category", { v -> CategoryExpenseQuery(Category(v), StringComparison.CONTAINS, StringCase.CASE_INSENSITIVE) } ),
    AMOUNT("amount", { v -> AmountExpenseQuery(Amount(v.toBigDecimal()), MathComparison.EQ) } ),
    DATE("date", { v -> DateExpenseQuery(LocalDate.parse(v), MathComparison.EQ) } );


    fun createQuery(value: String): ExpenseQuery {
        return factory.invoke(value)
    }
}