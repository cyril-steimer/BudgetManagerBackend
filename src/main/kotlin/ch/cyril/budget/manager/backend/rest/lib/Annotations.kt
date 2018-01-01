package ch.cyril.budget.manager.backend.rest.lib

annotation class Body

annotation class Header(val name: String)

annotation class PathParam(val name: String)

annotation class QueryParam(val name: String)