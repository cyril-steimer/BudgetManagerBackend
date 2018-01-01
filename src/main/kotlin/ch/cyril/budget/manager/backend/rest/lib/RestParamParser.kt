package ch.cyril.budget.manager.backend.rest.lib

import java.lang.reflect.Type

interface RestParamParser {

    fun <T> parse(value: String, type: Class<T>): T

    fun <T> parse(value: String, type: Type): T
}