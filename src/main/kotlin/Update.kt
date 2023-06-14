import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

fun main() {

    runBlocking {
        setupConnection()?.let { db: MongoDatabase ->
            updateSingleDocument(db)
            updateMultipleDocuments(db)
        }
    }
}


suspend fun updateSingleDocument(db: MongoDatabase) {
    val collection = db.getCollection<Restaurant>("restaurants")
    val queryParam = Filters.eq("restaurant_id", "restaurantId")
    val updateParams = Updates.set("restaurant_id", Random.nextInt().toString())

    collection.updateOne(filter = queryParam, update = updateParams).also {
        println("Total docs modified ${it.matchedCount} and fields modified ${it.modifiedCount}")
    }
}


suspend fun updateMultipleDocuments(db: MongoDatabase) {
    val collection = db.getCollection<Restaurant>("restaurants")
    val queryParam = Filters.eq(Restaurant::cuisine.name,"American")
    val updateParams = Updates.set(Restaurant::cuisine.name, "Indian")

    collection.updateMany(filter = queryParam, update = updateParams).also {
        println("Total docs modified ${it.matchedCount} and fields modified ${it.modifiedCount}")
    }

}
