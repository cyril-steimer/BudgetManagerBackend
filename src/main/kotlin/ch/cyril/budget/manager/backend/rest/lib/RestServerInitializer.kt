package ch.cyril.budget.manager.backend.rest.lib

import ch.cyril.budget.manager.backend.main.ServerConfig
import ch.cyril.budget.manager.backend.rest.BudgetRestHandler
import ch.cyril.budget.manager.backend.rest.ExpenseRestHandler
import ch.cyril.budget.manager.backend.rest.ImportExportRestHandler
import ch.cyril.budget.manager.backend.rest.UtilsRestHandler
import ch.cyril.budget.manager.backend.service.ServiceFactory

abstract class RestServerInitializer {

    fun startServer(serviceFactory: ServiceFactory, config: ServerConfig, paramParser: RestParamParser): RestServer<*> {
        val server = doStartServer(config)

        val expenseDao = serviceFactory.createExpenseDao()
        val templateDao = serviceFactory.createTemplateDao()
        val budgetDao = serviceFactory.createBudgetDao()

        RestMethodRegisterer(server, paramParser, ExpenseRestHandler(expenseDao, "expenses")).register()
        RestMethodRegisterer(server, paramParser, ExpenseRestHandler(templateDao, "templates")).register()
        RestMethodRegisterer(server, paramParser, UtilsRestHandler(expenseDao)).register()
        RestMethodRegisterer(server, paramParser, BudgetRestHandler(budgetDao)).register()
        RestMethodRegisterer(server, paramParser, ImportExportRestHandler(budgetDao, expenseDao)).register()

        println("Started server on port ${config.port}")

        return server
    }

    protected abstract fun doStartServer(config: ServerConfig): RestServer<*>
}