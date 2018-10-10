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