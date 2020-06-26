package ch.cyril.budget.manager.backend.model

import ch.cyril.budget.manager.backend.util.Identifiable
import ch.cyril.budget.manager.backend.util.IdentifiableTypeAdapter
import com.google.gson.annotations.JsonAdapter

class BudgetFilter(
        val blacklist: List<Budget>?,
        val whitelist: List<Budget>?)

data class Title(val title: String)

data class Order(val order: Int)

@JsonAdapter(ViewPeriodTypeAdapter::class)
enum class ViewPeriod(override val identifier: String) : Identifiable {
    MONTHLY("monthly"),
    YEARLY("yearly"),
    FIXED("fixed");
}

class ViewPeriodTypeAdapter : IdentifiableTypeAdapter<ViewPeriod>(ViewPeriod::class)

@JsonAdapter(ViewTypeTypeAdapter::class)
enum class ViewType(override val identifier: String) : Identifiable {
    BUDGET_VIEW("budgetView"),
    EXPENSE_VIEW("expenseView")
}

class ViewTypeTypeAdapter : IdentifiableTypeAdapter<ViewType>(ViewType::class)

data class View(
        val id: Id,
        val type: ViewType,
        val order: Order,
        val title: Title,
        val budgetFilter: BudgetFilter?,
        val period: ViewPeriod,
        val drillDownViewId: Id?,
        val drillUpViewId: Id?,
        val start: MonthYear?,
        val end: MonthYear?)
