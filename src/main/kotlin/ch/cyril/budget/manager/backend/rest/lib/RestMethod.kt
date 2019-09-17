package ch.cyril.budget.manager.backend.rest.lib

import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KTypeParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.instanceParameter

class RestMethod private constructor(
        private val owner: Any,
        private val method: RestHandlerMethod,
        private val parser: RestParamParser) {

    suspend fun invoke(ctx: RestContext): RestResult? {
        val params = gatherParams(ctx)
        val res = doInvoke(method.function, params)
        if (res is RestResult) {
            return res
        }
        return null
    }

    fun verb(): HttpVerb {
        return method.verb
    }

    fun path(): RestMethodPath {
        return RestMethodPath.parse(method.path)
    }

    private suspend fun doInvoke(func: KFunction<*>, params: Array<Any?>): Any? {
        if (func.instanceParameter == null) {
            return func.call(*params)
        }
        return func.call(owner, *params)
    }

    private suspend fun gatherParams(ctx: RestContext): Array<Any?> {
        return method.function.parameters
                .filter { p -> p.name != null }
                .map { p -> getParamValue(p, ctx) }
                .toTypedArray()
    }


    private suspend fun getParamValue(param: KParameter, ctx: RestContext): Any? {
        var value = getRawParamValue(param, ctx)
        value = parseParamValue(param.type.classifier as KClass<*>, value)
        validateParamValue(param, value)
        return value
    }

    private fun parseParamValue(cls: KClass<*>, value: Any?): Any? {
        //TODO The entire parsing mechanism should be improved.
        if (value !is String) {
            return value
        } else if (cls == String::class) {
            return value
        }
        return parser.parse(value, cls.java)
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

    private suspend fun getRawParamValue(param: KParameter, ctx: RestContext): Any? {
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

    companion object {
        fun of(owner: Any, function: KFunction<*>, parser: RestParamParser): RestMethod? {
            val method = function.findAnnotation<HttpMethod>()
            if (method == null) {
                return null
            }
            val handlerMethod = RestHandlerMethod(function, method.verb, method.path)
            return of(owner, handlerMethod, parser)
        }

        fun of (owner: Any, method: RestHandlerMethod, parser: RestParamParser): RestMethod? {
            val returnType = method.function.returnType.classifier
            if (returnType != RestResult::class && returnType != Unit::class) {
                return null
            }
            return RestMethod(owner, method, parser)
        }
    }
}