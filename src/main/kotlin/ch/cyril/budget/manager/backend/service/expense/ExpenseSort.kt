package ch.cyril.budget.manager.backend.service.expense

import ch.cyril.budget.manager.backend.model.Expense
import ch.cyril.budget.manager.backend.model.Timestamp
import ch.cyril.budget.manager.backend.service.SortDirection
import ch.cyril.budget.manager.backend.util.Identifiable
import java.math.BigDecimal
import java.time.LocalDate

class ExpenseSort(val field: ExpenseSortField, val direction: SortDirection)

enum class ExpenseSortField(override val identifier: String, val sorter: Comparator<Expense>) : Identifiable {

    ID("id", Comparator.comparing<Expense, Int> { e -> e.id.id }),
    AMOUNT("amount", Comparator.comparing<Expense, BigDecimal> { e -> e.amount.amount }),
    NAME("name", Comparator.comparing<Expense, String> { e -> e.name.name }),
    CATEGORY("category", Comparator.comparing<Expense, String> { e -> e.category.name }),
    DATE("date", Comparator.comparing<Expense, Long> { e -> e.date.timestamp });

    companion object {

        fun sort(field: ExpenseSortField?, direction: SortDirection?): ExpenseSort? {
            val dir = direction ?: SortDirection.ASCENDING
            if (field != null) {
                return ExpenseSort(field, dir)
            }
            return null
        }
    }
}