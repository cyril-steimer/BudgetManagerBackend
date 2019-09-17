package ch.cyril.budget.manager.backend.service.expense

import ch.cyril.budget.manager.backend.model.ScheduledExpense
import ch.cyril.budget.manager.backend.model.Timestamp
import org.slf4j.LoggerFactory
import java.lang.Exception

/**
 * Instantiates scheduled expenses. This runnable is meant to be run periodically (e.g. daily after midnight) to ensure
 * that scheduled expenses are regularly instantiated.
 *
 * If the BudgetManager did not run for a while, this runnable will ensure that all missing expenses are generated.
 */
class ScheduledExpenseInstantiator(
        private val scheduledExpenseDao: ScheduledExpenseDao,
        private val actualExpenseDao: ActualExpenseDao) : Runnable {

    companion object {
        val LOG = LoggerFactory.getLogger(ScheduledExpenseInstantiator::class.java)
    }

    override fun run() {
        try {
            run(Timestamp.now())
        } catch (e: Exception) {
            LOG.error("Failed to instantiate scheduled expenses.", e)
        }
    }

    fun run(today: Timestamp) {
        val expenses = scheduledExpenseDao.getExpenses().values
        for (expense in expenses) {
            instantiateExpenses(today, expense)
        }
    }

    private fun instantiateExpenses(today: Timestamp, scheduled: ScheduledExpense) {
        var from = scheduled.startDate
        if (scheduled.lastExpense != null) {
            from = Timestamp.ofEpochDay(scheduled.lastExpense.date.getEpochDay() + 1)
        }
        while (canPossiblyAddMoreExpenses(scheduled, today, from)) {
           from = tryInstantiateOneExpense(today, scheduled, from)
        }
    }

    private fun tryInstantiateOneExpense(today: Timestamp, scheduled: ScheduledExpense, from: Timestamp): Timestamp {
        val next = scheduled.schedule.getNextDate(from)
        if (next.getEpochDay() <= today.getEpochDay()) {
            val newExpense = this.actualExpenseDao.addExpense(scheduled.getExpenseToAdd(next))
            val updated = scheduled.copy(lastExpense = newExpense)
            scheduledExpenseDao.updateExpense(updated)
        }
        return Timestamp.ofEpochDay(next.getEpochDay() + 1)
    }

    private fun canPossiblyAddMoreExpenses(scheduled: ScheduledExpense, today: Timestamp, from: Timestamp): Boolean {
        return from.getEpochDay() <= today.getEpochDay()
            && (scheduled.endDate == null || from.getEpochDay() <= scheduled.endDate.getEpochDay())
    }
}