package ch.cyril.budget.manager.backend.service.expense

import ch.cyril.budget.manager.backend.util.Identifiable

class ExpenseSort(val field: ExpenseSortField, val direction: SortDirection)

enum class SortDirection(override val identifier: String) : Identifiable {
    ASCENDING("asc"),
    DESCENDING("desc");

    fun <A, R> switch(switch: SortDirectionSwitch<A, R>, arg: A): R {
        if (this == ASCENDING) {
            return switch.caseAscending(arg)
        }
        if (this == DESCENDING) {
            return switch.caseDescending(arg)
        }
        throw IllegalStateException()
    }
}

interface SortDirectionSwitch<A, R> {

    fun caseAscending(arg: A): R

    fun caseDescending(arg: A): R
}

enum class ExpenseSortField(override val identifier: String) : Identifiable {
    ID("id"),
    AMOUNT("amount"),
    NAME("name"),
    CATEGORY("category"),
    DATE("date");

    fun <A, R> switch(switch: ExpenseSortFieldSwitch<A, R>, arg: A): R {
        if (this == ID) {
            return switch.caseId(arg)
        }
        if (this == AMOUNT) {
            return switch.caseAmount(arg)
        }
        if (this == NAME) {
            return switch.caseName(arg)
        }
        if (this == CATEGORY) {
            return switch.caseCategory(arg)
        }
        if (this == DATE) {
            return switch.caseDate(arg)
        }
        throw IllegalStateException()
    }

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

interface ExpenseSortFieldSwitch<A, R> {

    fun caseId(arg: A): R

    fun caseAmount(arg: A): R

    fun caseName(arg: A): R

    fun caseCategory(arg: A): R

    fun caseDate(arg: A): R
}