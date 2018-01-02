package ch.cyril.budget.manager.backend.service

import ch.cyril.budget.manager.backend.util.Identifiable

enum class StringCase(override val identifier: String) : Identifiable {
    CASE_SENSITIVE("caseSensitive"),
    CASE_INSENSITIVE("caseInsensitive");

    fun <A, R> switch(switch: StringCaseSwitch<A, R>, arg: A): R {
        if (this == CASE_SENSITIVE) {
            return switch.caseCaseSensitive(arg)
        }
        if (this == CASE_INSENSITIVE) {
            return switch.caseCaseInsensitive(arg)
        }
        throw IllegalStateException()
    }
}

interface StringCaseSwitch<A, R> {

    fun caseCaseSensitive(arg: A): R

    fun caseCaseInsensitive(arg: A): R
}

enum class StringComparison(override val identifier: String) : Identifiable {
    STARTS_WITH("startsWith"),
    CONTAINS("contains"),
    ENDS_WITH("endsWith"),
    EQ("eq");

    fun <A, R> switch(switch: StringComparisonSwitch<A, R>, arg: A): R {
        if (this == STARTS_WITH) {
            return switch.caseStartsWith(arg)
        }
        if (this == CONTAINS) {
            return switch.caseContains(arg)
        }
        if (this == ENDS_WITH) {
            return switch.caseEndsWith(arg)
        }
        if (this == EQ) {
            return switch.caseEq(arg)
        }
        throw IllegalStateException()
    }
}

interface StringComparisonSwitch<A, R> {

    fun caseStartsWith(arg: A): R

    fun caseContains(arg: A): R

    fun caseEndsWith(arg: A): R

    fun caseEq(arg: A): R
}

enum class MathComparison(override val identifier: String) : Identifiable {
    LT("<"),
    LTE("<="),
    EQ("=="),
    NEQ("!="),
    GTE(">="),
    GT(">");

    fun <A, R> switch(switch: MathComparisonSwitch<A, R>, arg: A): R {
        if (this == LT) {
            return switch.caseLt(arg)
        }
        if (this == LTE) {
            return switch.caseLte(arg)
        }
        if (this == EQ) {
            return switch.caseEq(arg)
        }
        if (this == NEQ) {
            return switch.caseNeq(arg)
        }
        if (this == GTE) {
            return switch.caseGte(arg)
        }
        if (this == GT) {
            return switch.caseGt(arg)
        }
        throw IllegalStateException()
    }
}

interface MathComparisonSwitch<A, R> {

    fun caseLt(arg: A): R

    fun caseLte(arg: A): R

    fun caseEq(arg: A): R

    fun caseNeq(arg: A): R

    fun caseGte(arg: A): R

    fun caseGt(arg: A): R
}

fun main(args: Array<String>) {
    val clsName = "ch.cyril.budget.manager.backend.service.MathComparison"
    val cls = Class.forName(clsName)

    val switchCls = StringBuilder()
    val switchClsName = cls.simpleName + "Switch<A, R>"
    switchCls.append("interface ").append(switchClsName).append(" {")

    for (const in cls.enumConstants) {
        switchCls.append("\n\n    ").append("fun ").append(switchMethodName(const as Enum<*>)).append("(arg: A): R")
    }
    switchCls.append("\n}")

    println(switchCls)

    val switchMethod = StringBuilder()
    switchMethod.append("fun <A, R> switch(switch: ").append(switchClsName).append(", arg: A): R {")

    for (const in cls.enumConstants) {
        switchMethod.append("\n    if (this == ").append((const as Enum<*>).name).append(") { \n")
                .append("        return switch.").append(switchMethodName(const)).append("(arg)\n")
                .append("    }")
    }
    switchMethod.append("\n    throw IllegalStateException()\n}")

    println(switchMethod)
}

private fun switchMethodName(const: Enum<*>): String {
    return "case" + enumNameCamelCase(const)
}

private fun enumNameCamelCase(const: Enum<*>): String {
    var capitalizeNext = true
    val name = const.name
    val res = StringBuilder()
    for (i in 0 until name.length) {
        val char = name[i]
        if (char == '_') {
            capitalizeNext = true
            continue
        }
        if (capitalizeNext) {
            res.append(char)
        } else {
            res.append(char.toLowerCase())
        }
        capitalizeNext = false
    }
    return res.toString()
}