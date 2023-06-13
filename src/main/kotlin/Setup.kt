import com.mongodb.client.model.CreateCollectionOptions
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.runBlocking
import java.util.*

fun main() {
    val databaseName = "sample_restaurants"
    val database = setupConnection(databaseName = databaseName, "MONGODB_URI")
    runBlocking {
        listAllCollection(database = database)

        createCollection(database = database)

        listAllCollection(database = database)

        dropCollection(database = database)

        listAllCollection(database = database)
    }
}

fun setupConnection(
    databaseName: String = "sample_restaurants",
    connectionEnvVariable: String = "MONGODB_URI"
):
        MongoDatabase {
    val connectString = if (System.getenv(connectionEnvVariable) != null) {
        System.getenv(connectionEnvVariable)
    } else {
        "mongodb+srv://mohitsharma:<enter your password>@cluster0.sq3aiau.mongodb" +
                ".net/?retryWrites=true&w=majority"
    }
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




