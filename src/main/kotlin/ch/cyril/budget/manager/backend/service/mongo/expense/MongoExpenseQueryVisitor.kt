package ch.cyril.budget.manager.backend.service.mongo.expense

import ch.cyril.budget.manager.backend.service.*
import ch.cyril.budget.manager.backend.service.expense.*
import com.mongodb.client.model.Filters.*;
import ch.cyril.budget.manager.backend.service.mongo.*;
import org.bson.conversions.Bson
import java.util.regex.Pattern

class MongoExpenseQueryVisitor : ExpenseQueryVisitor<Unit, Bson> {

    private class CaseSensitiveFlags : StringCaseSwitch<Unit, Int> {
        override fun caseCaseSensitive(arg: Unit): Int {
            return 0
        }

        override fun caseCaseInsensitive(arg: Unit): Int {
            return Pattern.CASE_INSENSITIVE
        }
    }

    private class StringComparisonRegex: StringComparisonSwitch<String, String> {

        override fun caseStartsWith(arg: String): String {
            return "^$arg"
        }

        override fun caseContains(arg: String): String {
            return arg
        }

        override fun caseEndsWith(arg: String): String {
            return "$arg$"
        }

        override fun caseEq(arg: String): String {
            return "^$arg$"
        }
    }

    private class MathComparisonFilter<A>(val field: String): MathComparisonSwitch<A, Bson> {
        override fun caseLt(arg: A): Bson {
            return lt(field, arg)
        }

        override fun caseLte(arg: A): Bson {
            return lte(field, arg)
        }

        override fun caseEq(arg: A): Bson {
            return eq(field, arg)
        }

        override fun caseNeq(arg: A): Bson {
            return not(eq(field, arg))
        }

        override fun caseGte(arg: A): Bson {
            return gte(field, arg)
        }

        override fun caseGt(arg: A): Bson {
            return gt(field, arg)
        }
    }

    override fun visitNameQuery(query: NameExpenseQuery, arg: Unit): Bson {
        return regex(
                KEY_NAME,
                createPatternWithCaseAndComparison(query.name.name, query.comparison, query.case))
    }

    override fun visitMethodQuery(query: MethodExpenseQuery, arg: Unit): Bson {
        return regex(
                KEY_METHOD,
                createPatternWithCaseAndComparison(query.method.name, query.comparison, query.case))
    }

    override fun visitIdQuery(query: IdExpenseQuery, arg: Unit): Bson {
        return eq(KEY_ID, query.id.id)
    }

    override fun visitCategoryQuery(query: CategoryExpenseQuery, arg: Unit): Bson {
        return regex(
                KEY_CATEGORY,
                createPatternWithCaseAndComparison(query.category.name, query.comparison, query.case))
    }

    override fun visitDateQuery(query: DateExpenseQuery, arg: Unit): Bson {
        return query.comparison.switch(MathComparisonFilter(KEY_DATE), query.date.timestamp)
    }

    override fun visitAmountQuery(query: AmountExpenseQuery, arg: Unit): Bson {
        return query.comparison.switch(MathComparisonFilter(KEY_AMOUNT), query.amount.amount)
    }

    override fun visitTagQuery(query: TagExpenseQuery, arg: Unit): Bson {
        return eq(KEY_TAGS, query.tag.name)
    }

    override fun visitOrQuery(query: OrExpenseQuery, arg: Unit): Bson {
        val queries = query.queries.map { q -> q.accept(this, Unit) }
        return or(queries)
    }

    override fun visitAndQuery(query: AndExpenseQuery, arg: Unit): Bson {
        val queries = query.queries.map { q -> q.accept(this, Unit) }
        return and(queries)
    }

    private fun createPatternWithCaseAndComparison(search: String, comparison: StringComparison, case: StringCase): Pattern {
        val regex = Pattern.quote(search)
        return Pattern.compile(
                comparison.switch(StringComparisonRegex(), regex),
                case.switch(CaseSensitiveFlags(), Unit))
    }
}