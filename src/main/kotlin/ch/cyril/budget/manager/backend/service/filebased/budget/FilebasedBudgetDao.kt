package ch.cyril.budget.manager.backend.service.filebased.budget

import ch.cyril.budget.manager.backend.model.Budget
import ch.cyril.budget.manager.backend.service.budget.BudgetDao
import ch.cyril.budget.manager.backend.util.SubList
import java.nio.file.Path

class FilebasedBudgetDao(val file: Path) : BudgetDao {

    private val parser = BudgetParser()

    override fun getBudgets(): SubList<Budget> {
        return SubList.Companion.of(parser.load(file))
    }

    override fun addBudget(budget: Budget) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateBudget(budget: Budget) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteBudget(budget: Budget) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}