package ch.cyril.budget.manager.backend.systemtest

import ch.cyril.budget.manager.backend.rest.GSON
import com.google.gson.reflect.TypeToken
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import kotlinx.coroutines.runBlocking

class HttpClient(private val port: Int) {

    fun put (path: String, body: Any) {
        val client = HttpClient()
        runBlocking {
            client.put<Unit>(getUrl(path)) {
                this.body = GSON.toJson(body)
            }
        }
    }

    fun post (path: String, body: Any) {
        val client = HttpClient()
        runBlocking {
            client.post<Unit>(getUrl(path)) {
                this.body = GSON.toJson(body)
            }
        }
    }

    fun delete (path: String) {
        val client = HttpClient()
        runBlocking {
            client.delete<Unit>(getUrl(path))
        }
    }

    inline fun <reified T> postJson (path: String, body: Any): T {
        val client = HttpClient()
        val type = object: TypeToken<T>() {}.type
        return runBlocking {
            val json = client.post<String>(getUrl(path)) {
                this.body = GSON.toJson(body)
            }
            GSON.fromJson<T>(json, type)
        }
    }

    inline fun <reified T> getJson (path: String): T {
        val client = HttpClient()
        val type = object: TypeToken<T>() {}.type
        return runBlocking {
            val json = client.get<String>(getUrl(path))
            GSON.fromJson<T>(json, type)
        }
    }

    fun getUrl (path: String) = "http://127.0.0.1:$port$path"
}