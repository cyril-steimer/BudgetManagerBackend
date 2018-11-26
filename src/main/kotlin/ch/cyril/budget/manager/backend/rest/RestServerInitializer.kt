package ch.cyril.budget.manager.backend.rest

import ch.cyril.budget.manager.backend.main.Config
import ch.cyril.budget.manager.backend.main.ServerConfig
import ch.cyril.budget.manager.backend.rest.lib.RestMethodRegisterer
import ch.cyril.budget.manager.backend.rest.lib.RestParamParser
import ch.cyril.budget.manager.backend.rest.lib.RestServer
import ch.cyril.budget.manager.backend.service.ServiceFactory

abstract class RestServerInitializer(
        private val serviceFactory: ServiceFactory,
        protected val config: ServerConfig,
        private val paramParser: RestParamParser) {

    fun startServer() {
        val server = doStartServer()

        val expenseDao = serviceFactory.createExpenseDao()
        val budgetDao = serviceFactory.createBudgetDao()

        RestMethodRegisterer(server, paramParser, ExpenseRestHandler(expenseDao)).register()
        RestMethodRegisterer(server, paramParser, BudgetRestHandler(budgetDao)).register()
    }

    protected abstract fun doStartServer(): RestServer
}