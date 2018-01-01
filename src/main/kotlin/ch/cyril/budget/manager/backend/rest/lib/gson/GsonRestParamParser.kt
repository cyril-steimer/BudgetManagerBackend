package ch.cyril.budget.manager.backend.rest.lib.gson

import ch.cyril.budget.manager.backend.rest.lib.RestParamParser
import com.google.gson.Gson
import java.lang.reflect.Type

class GsonRestParamParser(private val gson: Gson) : RestParamParser {

    override fun <T> parse(value: String, type: Class<T>): T {
        return gson.fromJson(value, type)
    }

    override fun <T> parse(value: String, type: Type): T {
        return gson.fromJson(value, type)
    }
}