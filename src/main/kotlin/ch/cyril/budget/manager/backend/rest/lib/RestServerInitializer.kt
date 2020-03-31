package ch.cyril.budget.manager.backend.rest.lib

import ch.cyril.budget.manager.backend.main.ServerConfig
import ch.cyril.budget.manager.backend.rest.*
import ch.cyril.budget.manager.backend.service.ServiceFactory
import ch.cyril.budget.manager.backend.service.expense.ScheduledExpenseInstantiator
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

abstract class RestServerInitializer {

    fun startServer(serviceFactory: ServiceFactory, config: ServerConfig, paramParser: RestParamParser): RestServer<*> {
        val server = doStartServer(config)

        val expenseDao = serviceFactory.createExpenseDao()
        val templateDao = serviceFactory.createTemplateDao()
        val scheduledExpensesDao = serviceFactory.createScheduledExpenseDao()
        val budgetDao = serviceFactory.createBudgetDao()

        RestMethodRegisterer(server, paramParser, ActualExpenseRestHandler(expenseDao)).register()
        RestMethodRegisterer(server, paramParser, ExpenseTemplateRestHandler(templateDao)).register()
        RestMethodRegisterer(server, paramParser, ScheduledExpenseRestHandler(scheduledExpensesDao)).register()
        RestMethodRegisterer(server, paramParser, UtilsRestHandler(expenseDao)).register()
        RestMethodRegisterer(server, paramParser, BudgetRestHandler(budgetDao)).register()
        RestMethodRegisterer(server, paramParser, ImportExportRestHandler(budgetDao, expenseDao, templateDao, scheduledExpensesDao)).register()

        val scheduledExpenseInstantiator = ScheduledExpenseInstantiator(scheduledExpensesDao, expenseDao)
        val scheduledExecutor = Executors.newScheduledThreadPool(1)
        scheduledExecutor.scheduleAtFixedRate(scheduledExpenseInstantiator, 1, 1, TimeUnit.HOURS)

        println("Started server on port ${config.port}")

        return server
    }

    protected abstract fun doStartServer(config: ServerConfig): RestServer<*>
}