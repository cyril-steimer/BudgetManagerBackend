package ch.cyril.budget.manager.backend.service.mongo

import com.mongodb.client.model.Updates.*
import org.bson.Document
import org.bson.conversions.Bson

class MongoUtil {

    fun toUpdate(doc: Document): Bson {
        val setters = ArrayList<Bson>()
        for (key in doc.keys) {
            val value = doc[key]
            setters.add(set(key, value))
        }
        return combine(setters)
    }
}