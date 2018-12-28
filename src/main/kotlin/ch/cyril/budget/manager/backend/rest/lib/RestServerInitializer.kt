package ch.cyril.budget.manager.backend.rest.lib

import ch.cyril.budget.manager.backend.main.ServerConfig
import ch.cyril.budget.manager.backend.rest.BudgetRestHandler
import ch.cyril.budget.manager.backend.rest.ExpenseRestHandler
import ch.cyril.budget.manager.backend.rest.ImportExportRestHandler
import ch.cyril.budget.manager.backend.service.ServiceFactory

abstract class RestServerInitializer {

    fun startServer(serviceFactory: ServiceFactory, config: ServerConfig, paramParser: RestParamParser) {
        val server = doStartServer(config)

        val expenseDao = serviceFactory.createExpenseDao()
        val budgetDao = serviceFactory.createBudgetDao()

        RestMethodRegisterer(server, paramParser, ExpenseRestHandler(expenseDao)).register()
        RestMethodRegisterer(server, paramParser, BudgetRestHandler(budgetDao)).register()
        RestMethodRegisterer(server, paramParser, ImportExportRestHandler(budgetDao, expenseDao)).register()

        println("Started server on port ${config.port}")
    }

    protected abstract fun doStartServer(config: ServerConfig): RestServer<*>
}