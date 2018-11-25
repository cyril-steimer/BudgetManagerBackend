package ch.cyril.budget.manager.backend.rest.lib.ktor

import ch.cyril.budget.manager.backend.main.ServerConfig
import ch.cyril.budget.manager.backend.rest.BudgetRestHandler
import ch.cyril.budget.manager.backend.rest.ExpenseRestHandler
import ch.cyril.budget.manager.backend.rest.lib.RestMethodRegisterer
import ch.cyril.budget.manager.backend.rest.lib.RestParamParser
import ch.cyril.budget.manager.backend.service.ServiceFactory
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

class KtorRestServerInitializer(
        private val serviceFactory: ServiceFactory,
        private val config: ServerConfig,
        private val paramParser: RestParamParser) {

    fun startServer() {
        val server = embeddedServer(Netty, config.port) { }
        server.start(wait = false)

        val expenseDao = serviceFactory.createExpenseDao()
        val budgetDao = serviceFactory.createBudgetDao()


        val res = KtorRestServer(server)
        RestMethodRegisterer(res, paramParser, ExpenseRestHandler(expenseDao)).register()
        RestMethodRegisterer(res, paramParser, BudgetRestHandler(budgetDao)).register()
    }
}