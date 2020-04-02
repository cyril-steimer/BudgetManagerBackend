package ch.cyril.budget.manager.backend.service

class Pagination(val from: Int, val count: Int) {

    companion object {
        fun of(from: Int?, count: Int?): Pagination? {
            if (from != null && count != null) {
                return Pagination(from, count)
            }
            return null
        }
    }
}