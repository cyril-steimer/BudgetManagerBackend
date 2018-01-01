package ch.cyril.budget.manager.backend.service.expense

import ch.cyril.budget.manager.backend.model.Amount
import ch.cyril.budget.manager.backend.model.Expense
import ch.cyril.budget.manager.backend.model.Id
import ch.cyril.budget.manager.backend.model.Name
import ch.cyril.budget.manager.backend.service.SortDirection
import java.math.BigDecimal
import java.time.LocalDate

class ExpenseSort(val field: ExpenseSortField, val direction: SortDirection)

enum class ExpenseSortField(val fieldName: String, val sorter: Comparator<Expense>) {

    ID("id", Comparator.comparing<Expense, Int> { e -> e.id.id }),
    AMOUNT("amount", Comparator.comparing<Expense, BigDecimal> { e -> e.amount.amount }),
    NAME("name", Comparator.comparing<Expense, String> { e -> e.name.name }),
    CATEGORY("category", Comparator.comparing<Expense, String> { e -> e.category.name }),
    DATE("date", Comparator.comparing<Expense, LocalDate> { e -> e.date });

    companion object {
        fun byFieldName(name: String): ExpenseSortField {
            val res = values()
                    .find { f -> f.fieldName.equals(name) }
            return res ?: throw IllegalArgumentException("No sortable expense field '$name'")
        }

        fun sort(field: ExpenseSortField?, direction: SortDirection?): ExpenseSort? {
            val dir = direction ?: SortDirection.ASCENDING
            if (field != null) {
                return ExpenseSort(field, dir)
            }
            return null
        }
    }
}