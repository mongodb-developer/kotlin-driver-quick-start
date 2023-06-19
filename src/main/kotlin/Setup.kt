import com.mongodb.MongoException
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.runBlocking
import org.bson.BsonInt64
import org.bson.Document
import java.util.*

fun main() {
    val databaseName = "sample_restaurants"

    runBlocking {

        val database = setupConnection(databaseName = databaseName, "MONGODB_URI")

        if (database != null) {
            listAllCollection(database = database)

            listAllCollection(database = database)

            dropCollection(database = database)

            listAllCollection(database = database)
        }
    }
}

suspend fun setupConnection(
    databaseName: String = "sample_restaurants",
    connectionEnvVariable: String = "MONGODB_URI"
): MongoDatabase? {
    val connectString = if (System.getenv(connectionEnvVariable) != null) {
        System.getenv(connectionEnvVariable)
    } else {
        // Not a good practice only for demo purpose.
        "mongodb+srv://<username>:<password>@cluster0.sq3aiau.mongodb.net/?retryWrites=true&w=majority"
    }

    val client = MongoClient.create(connectionString = connectString)
    val database = client.getDatabase(databaseName = databaseName)

    return try {
        // Send a ping to confirm a successful connection
        val command = Document("ping", BsonInt64(1))
        database.runCommand(command)
        println("Pinged your deployment. You successfully connected to MongoDB!")
        database
    } catch (me: MongoException) {
        System.err.println(me)
        null
    }
}

suspend fun listAllCollection(database: MongoDatabase) {

    val count = database.listCollectionNames().count()
    println("Collection count $count")

    print("Collection in this database are ---------------> ")
    database.listCollectionNames().collect { print(" $it") }
    println()
}

suspend fun dropCollection(database: MongoDatabase) {
    database.getCollection<Objects>(collectionName = "collectionName").drop()
}




