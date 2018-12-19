package ch.cyril.budget.manager.backend.rest.lib


class RestResult(val contentType: String, val content: String) {

    companion object {
        fun json(json: String): RestResult {
            return RestResult("application/json", json)
        }
    }
}