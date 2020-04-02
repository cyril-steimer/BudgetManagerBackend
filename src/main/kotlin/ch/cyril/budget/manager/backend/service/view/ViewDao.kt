package ch.cyril.budget.manager.backend.service.view

import ch.cyril.budget.manager.backend.model.BudgetView
import ch.cyril.budget.manager.backend.model.Id
import ch.cyril.budget.manager.backend.util.SubList

interface ViewDao {

    fun getBudgetViews(): SubList<BudgetView>

    fun getBudgetView(id: Id): BudgetView?
}