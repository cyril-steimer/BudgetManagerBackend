package ch.cyril.budget.manager.backend.service

import ch.cyril.budget.manager.backend.util.Identifiable

enum class SortDirection(override val identifier: String) : Identifiable {
    ASCENDING("asc"),
    DESCENDING("desc");

    fun <T> sort(sorter: Comparator<T>): Comparator<T> {
        if (this == DESCENDING) {
            return sorter.reversed()
        }
        return sorter
    }
}