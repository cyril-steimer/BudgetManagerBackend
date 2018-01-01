package ch.cyril.budget.manager.backend.rest.lib

import java.io.ByteArrayInputStream
import java.io.InputStream

class RestResult(val contentType: String, val data: InputStream) {

    companion object {
        fun json(json: String): RestResult {
            return RestResult(
                    "application/json",
                    ByteArrayInputStream(json.toByteArray(Charsets.UTF_8)))
        }
    }
}