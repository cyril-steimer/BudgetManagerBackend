package ch.cyril.budget.manager.backend.service

class SubList<T> private constructor(val count: Int, val values: List<T>) {

    companion object {
        fun <T> of(list: List<T>, from: Int, count: Int): SubList<T> {
            val fromCorr = fit(from, list)
            val toCorr = fit(fromCorr + count, list)
            val sublist = list.subList(fromCorr, toCorr)
            return SubList(list.size, sublist)
        }

        fun <T> of(list: List<T>): SubList<T> {
            return SubList(list.size, list)
        }

        private fun fit(index: Int, list: List<*>): Int {
            if (index < 0) {
                return 0
            } else if (index > list.size) {
                return list.size
            }
            return index
        }
    }
}