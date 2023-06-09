import com.mongodb.client.model.CreateCollectionOptions
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.runBlocking
import java.util.*

fun main() {
    println("Hello World!")
    val databaseName = "sample_restaurants"
    val database = setupConnection(databaseName = databaseName,"MONGODB_URI")
    runBlocking {
        listAllCollection(database = database)

        createCollection(database = database)

        listAllCollection(database = database)

        dropCollection(database = database)

        listAllCollection(database = database)
    }
}

fun setupConnection(databaseName: String): MongoDatabase {
    val connectString =
        "mongodb+srv://mohitsharma:enter ur passowrd@cluster0.sq3aiau.mongodb" +
                ".net/?retryWrites=true&w=majority"
    val client = MongoClient.create(connectionString = connectString)
    return client.getDatabase(databaseName = databaseName)
}

fun setupConnection(databaseName: String, connectionEnvVariable: String): MongoDatabase {
    val connectString = System.getenv(connectionEnvVariable)
    val client = MongoClient.create(connectionString = connectString)
    return client.getDatabase(databaseName = databaseName)
}

suspend fun listAllCollection(database: MongoDatabase) {

    print("Collection in this database are ---------------> ")
    database.listCollectionNames().collect { print(" $it") }
    println()
}

suspend fun createCollection(database: MongoDatabase) {
    database.createCollection(
        collectionName = "test",
        createCollectionOptions = CreateCollectionOptions().maxDocuments(100)
    )
}

suspend fun dropCollection(database: MongoDatabase) {
    database.getCollection<Objects>(collectionName = "test").drop()
}




