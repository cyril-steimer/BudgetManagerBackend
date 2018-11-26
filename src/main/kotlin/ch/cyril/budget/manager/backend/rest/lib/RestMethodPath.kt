package ch.cyril.budget.manager.backend.rest.lib

class RestMethodPath private constructor(private val segments: List<Segment>) {

    fun toPath(prefix: String, postfix: String): String {
        return segments.joinToString("/") { s -> s.toPathParam(prefix, postfix) }
    }

    companion object {
        fun parse(path: String): RestMethodPath {
            val segments = path.split('/')
                    .map { s -> toSegment(s) }
            return RestMethodPath(segments)
        }

        private fun toSegment(segment: String): Segment {
            if (segment.startsWith(":")) {
                return ParamSegment(segment.substring(1))
            }
            return StandardSegment(segment)
        }
    }
}

private interface Segment {
    fun toPathParam(prefix: String, postfix: String): String
}

private class StandardSegment(val string: String) : Segment {
    override fun toPathParam(prefix: String, postfix: String): String {
        return string
    }
}

private class ParamSegment(val param: String) : Segment {
    override fun toPathParam(prefix: String, postfix: String): String {
        return prefix + param + postfix
    }
}