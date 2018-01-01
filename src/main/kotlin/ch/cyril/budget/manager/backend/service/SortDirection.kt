package ch.cyril.budget.manager.backend.service

enum class SortDirection(val direction: String) {
    ASCENDING("asc"),
    DESCENDING("desc");

    fun <T> sort(sorter: Comparator<T>): Comparator<T> {
        if (this == DESCENDING) {
            return sorter.reversed()
        }
        return sorter
    }

    companion object {
        fun byDirection(dir: String): SortDirection {
            val res = values().find { d -> d.direction.equals(dir) }
            return res ?: throw IllegalArgumentException("No sort direction '$dir'")
        }
    }
}