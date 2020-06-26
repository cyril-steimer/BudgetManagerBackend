package ch.cyril.budget.manager.backend.service.view

import ch.cyril.budget.manager.backend.model.Id
import ch.cyril.budget.manager.backend.model.View
import ch.cyril.budget.manager.backend.model.ViewType
import ch.cyril.budget.manager.backend.util.SubList

interface ViewDao {

    fun getViews(type: ViewType): SubList<View>

    fun getView(id:Id): View?
}