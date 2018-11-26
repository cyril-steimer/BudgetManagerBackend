package ch.cyril.budget.manager.backend.rest.lib.javalin

import ch.cyril.budget.manager.backend.rest.lib.*
import io.javalin.Context
import io.javalin.Javalin
import kotlinx.coroutines.runBlocking

class JavalinRestServer(private val javalin: Javalin) : RestServer<Context>() {

    override fun registerGet(path: String, handler: Handler<Context>) {
        javalin.get(path) { ctx -> invokeHandler(ctx, handler) }
    }

    override fun registerPost(path: String, handler: Handler<Context>) {
        javalin.post(path) { ctx -> invokeHandler(ctx, handler) }
    }

    override fun registerPut(path: String, handler: Handler<Context>) {
        javalin.put(path) { ctx -> invokeHandler(ctx, handler) }
    }

    override fun registerDelete(path: String, handler: Handler<Context>) {
        javalin.delete(path) { ctx -> invokeHandler(ctx, handler) }
    }

    override fun toPath(path: RestMethodPath): String {
        return path.toPath(":", "")
    }

    override fun getRestContext(ctx: Context): RestContext {
        return JavalinRestContext(ctx)
    }

    private fun invokeHandler(ctx: Context, handler: Handler<Context>) {
        runBlocking {
            handler.invoke(ctx)
        }
    }
}