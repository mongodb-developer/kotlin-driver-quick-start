import com.mongodb.client.model.Filters
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.IndexOptions
import com.mongodb.client.model.Indexes
import com.mongodb.client.model.Sorts
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.runBlocking

fun main() {

    runBlocking {
        setupConnection()?.let { db: MongoDatabase ->
            readAnyDocument(database = db)
            readSpecificDocument(database = db)
            readWithPaging(database = db, offset = 2, pageSize = 2)
            readWithIndex(database = db)
        }
    }
}

suspend fun readAnyDocument(database: MongoDatabase) {
    val collection = database.getCollection<Restaurant>(collectionName = "restaurants")
    collection.find<Restaurant>().limit(1).collect {
        println(it)
    }
}

suspend fun readSpecificDocument(database: MongoDatabase) {
    val collection = database.getCollection<Restaurant>(collectionName = "restaurants")
    val queryParams = Filters
        .and(
            listOf(
                eq("cuisine", "American"),
                eq("borough", "Queens")
            )
        )


    collection
        .find<Restaurant>(queryParams)
        .limit(2)
        .collect {
            println(it)
        }

}

suspend fun readWithPaging(database: MongoDatabase, offset: Int, pageSize: Int) {
    val collection = database.getCollection<Restaurant>(collectionName = "restaurants")
    val queryParams = Filters
        .and(
            listOf(
                eq(Restaurant::cuisine.name, "American"),
                eq(Restaurant::borough.name, "Queens")
            )
        )

    collection
        .find<Restaurant>(queryParams)
        .limit(pageSize)
        .skip(offset)
        .collect {
            println(it)
        }
}

suspend fun readWithIndex(database: MongoDatabase) {
    val collection = database.getCollection<Restaurant>(collectionName = "restaurants")
    val options = IndexOptions().apply {
        this.name("restaurant_id_index")
        this.background(true)

    }

    collection.createIndex(
        keys = Indexes.ascending("restaurant_id"),
        options = options
    )

    val filters = Filters.gte("restaurant_id", "40000000")
    val sort = Sorts.ascending("restaurant_id")

    collection
        .find(filter = filters)
        .sort(sort = sort)
        .limit(200)
        .collect {
            println(it)
        }

}






