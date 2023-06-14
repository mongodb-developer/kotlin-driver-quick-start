import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.runBlocking
import java.util.regex.Pattern

fun main() {
    runBlocking {
        setupConnection()?.let { db: MongoDatabase ->
            deleteRestaurant(db)
            deleteRestaurants(db)
        }
    }

}

suspend fun deleteRestaurant(db: MongoDatabase) {
    val collection = db.getCollection<Restaurant>(collectionName = "restaurants")
    val queryParams = Filters.eq("restaurant_id", "restaurantId")

    collection.findOneAndDelete(filter = queryParams).also {
        it?.let {
            println(it)
        }
    }
}

suspend fun deleteRestaurants(db: MongoDatabase) {
    val collection = db.getCollection<Restaurant>(collectionName = "restaurants")

    val queryParams = Filters.or(
        listOf(
            Filters.regex(Restaurant::name.name, Pattern.compile("^Insert")),
            Filters.regex("restaurant_id", Pattern.compile("^restaurant"))
        )
    )
    collection.deleteMany(filter = queryParams).also {
        println("Document deleted : ${it.deletedCount}")
    }
}
