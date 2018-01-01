package ch.cyril.budget.manager.backend.rest.lib

import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation

class RestHandlerInvoker(private val parser: RestParamParser) {

    fun invoke(handler: KFunction<*>, ctx: RestContext) {
        val params = gatherParams(handler, ctx)
        val res = handler.call(*params)
        if (res is RestResult) {
            ctx.apply(res)
        }
    }

    private fun gatherParams(handler: KFunction<*>, ctx: RestContext): Array<Any?> {
        return handler.parameters
                .filter { p -> p.name != null }
                .map { p -> getParamValue(p, ctx) }
                .toTypedArray()
    }


    private fun getParamValue(param: KParameter, ctx: RestContext): Any? {
        var value = getRawParamValue(param, ctx)
        value = parseParamValue(param.type.classifier as KClass<*>, value)
        validateParamValue(param, value)
        return value
    }

    private fun parseParamValue(cls: KClass<*>, value: Any?): Any? {
        if (!String::class.isInstance(value)) {
            return value
        }
        return parser.parse(value as String, cls.java)
    }

    private fun validateParamValue(param: KParameter, value: Any?) {
        val nullable = param.type.isMarkedNullable
        if (!nullable && value == null) {
            throw IllegalArgumentException("Parameter '${param.name}' is not nullable")
        }
        val cls = param.type.classifier as KClass<*>
        if (value != null && !cls.isInstance(value)) {
            throw IllegalArgumentException("Value '$value' for param '$param' is of wrong type")
        }
    }

    private fun getRawParamValue(param: KParameter, ctx: RestContext): Any? {
        if (param.findAnnotation<Body>() != null) {
            if (param.type.classifier == ByteArray::class) {
                return ctx.getRawBody()
            }
            return ctx.getBody()
        }
        val header = param.findAnnotation<Header>()
        if (header != null) {
            return ctx.getHeader(header.name)
        }
        val pathParam = param.findAnnotation<PathParam>()
        if (pathParam != null) {
            return ctx.getPathParam(pathParam.name)
        }
        val queryParam = param.findAnnotation<QueryParam>()!!
        if (isArray(param.type.classifier as KClass<*>)) {
            return ctx.getQueryParams(queryParam.name)
        }
        return ctx.getQueryParam(queryParam.name)
    }

    private fun isArray(cls: KClass<*>): Boolean {
        return cls.qualifiedName!!.endsWith("kotlin.Array")
    }
}