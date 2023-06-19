[1]: https://www.mongodb.com/atlas/database

[2]: https://www.mongodb.com/docs/atlas/sample-data/

[3]: https://www.mongodb.com/cloud/atlas/register

[4]: https://www.mongodb.com/docs/drivers/kotlin/coroutine/current/

[5]: https://github.com/mongodb-developer/kotlin-driver-quick-start

# Getting Started With the MongoDB Kotlin Driver

> This is an introduction article on how build application in Kotlin using [MongoDB Atlas][1] and [MongoDB Kotlin driver][4], latest addition to
> list of official driver. We build together a CRUD application that cover the basics of how to use MongoDB as a database, while leveraging the 
> benefits of Kotlin as a programming languages like data classes, coroutines, flow, etc.

## Prerequisites

This is a getting-started article, therefore nothing much is needed as a prerequisite, but familiarity with Kotlin as a programming language would be
helpful.

Also, we need an [Atlas account][3], which is free forever. Create an account if you haven't got one, that provides MongoDB as a cloud database and
much more. Later in this tutorial, with this account we create a new cluster, load dataset and eventually query against it.

In general, MongoDB is an open-source, cross-platform, and distributed document database that allows building apps with flexible schema. In
case, you are not familiar with or would like a quick recap I would recommend exploring the
[MongoDB Jumpstart series](https://www.youtube.com/watch?v=RGfFpQF0NpE&list=PL4RCxklHWZ9v2lcat4oEVGQhZg6r4IQGV) to get familiar with MongoDB and
its various services in under 10 minutes or if you prefer to read then you can follow this
[guide](https://www.mongodb.com/docs/atlas/getting-started/).

And last, to aid our development activities, we would be using [Jetbrains IntelliJ IDEA (Community Edition)](https://www.jetbrains.com/idea/download/)
which has default support for Kotlin language.

---------------------

## MongoDB Kotlin driver vs MongoDB Realm Kotlin SDK ?

Before we start I would like to touch base on [Realm Kotlin SDK](https://www.mongodb.com/docs/realm/sdk/kotlin/), one of the SDK used to create
client-side mobile applications using the MongoDB ecosystem and shouldn't be confused with the [MongoDB Kotlin driver][4] for server-side
programming.[MongoDB Kotlin driver][4], a language driver enable you to interact with Atlas[1] ,a cloud database, seamlessly with the benefits of the
Kotlin language paradigm and is appropriate to create backend apps, scripts, etc.

---------------------

To make learning more meaningful and practical we would be building a CRUD application, feel free to check out our
[Github repo][5] if you would like to follow along together. So Without further ado let's
get started.

## Create a project

To create the project we can use the Project wizard which can be found under `File` menu options and then select `New` followed by `Project`. This
would open `New Project` screen, as shown below, then update the project and language to Kotlin.

![Project wizard](https://images.contentstack.io/v3/assets/blt39790b633ee0d5a7/bltbce8e7adda583e3e/648793838b23a5b3d5052b69/Screenshot_2023-06-12_at_09.34.09.png)

After the initial Gradle sync our project is ready to run so let's give it a try using the run icon in the menu bar or simply press CTRL + R on Mac.
Currently, our project wouldn't do much apart from printing `Hello World!` and arguments supplied but `BUILD SUCCESSFUL` message in the run
console is what we're looking for, which tells us that our project setup is complete.

![build success](https://images.contentstack.io/v3/assets/blt39790b633ee0d5a7/blt97a67a3d4a402196/64879383d40ad08ec16808a9/Screenshot_2023-06-12_at_13.42.38.png)

Now next step is to add the Kotlin driver to our project which would allow us to interact with [MongoDB Atlas][1].

---------------------

## Adding MongoDB Kotlin driver

Adding the driver to the project is simple and straightforward, just update the `dependencies` block with Kotlin driver dependency in the build file
i.e. `build.gradle` with these.

```groovy
dependencies {
    // Kotlin coroutine dependency
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4'

    // MongoDB Kotlin driver dependency
    implementation 'org.mongodb:mongodb-driver-kotlin-coroutine:4.10.0-SNAPSHOT'
}
```

And now we can connect with [MongoDB Atlas][1] using Kotlin driver.

---------------------

## Connecting to database

To connect with the database, we first need the `Connection URI` that can be found by pressing connect to cluster at
[Atlas account][3], as shown below.

![image](https://images.contentstack.io/v3/assets/blt39790b633ee0d5a7/blt1d92c6f1c6654b04/648793839625e14516b3657c/68747470733a2f2f6d6f6e676f64622d6465766875622d636d732e73332e75732d776573742d312e616d617a6f6e6177732e636f6d2f436f6e6e656374696f6e5f5552495f666439393037653262642e706e67.png)

For more details, you can also refer to this [documentation](https://www.mongodb.com/docs/guides/atlas/connection-string/).

With connection URI available, the next step is to create a Kotlin file, `Setup.kt` where we would be writing the code for connecting to [MongoDB
Atlas][1].

![Setup.kt file](https://images.contentstack.io/v3/assets/blt39790b633ee0d5a7/bltdc533d1983ce2f87/6488261e8b23a52669052cee/Screenshot_2023-06-13_at_09.17.29.png)

Connection with our database can be split into two steps first we create MongoClient instance using `Connection URI`.

```kotlin
val connectionString = "mongodb+srv://<username>:<enter your password>@cluster0.sq3aiau.mongodb.net/?retryWrites=true&w=majority"
val client = MongoClient.create(connectionString = connectString)
```

And second then with that, we can connect to the database, `sample_restaurants` which is sample dataset for restaurants. [Sample dataset][2] is
a great way of explore the platform and enabling to build more realistic POC to validate your idea's. To learn how to seed your first Atlas
database with sample data [here][2].

```kotlin
val databaseName = "sample_restaurants"
val db: MongoDatabase = client.getDatabase(databaseName = databaseName)
```

And since it's not good practice to hardcode `connectionString` in code, therefore let's move it outside. We would be using environment variables
for simplicity, but you can use Vault, build configuration variable or CI/CD environment variable whatever suites you well.

To add environment variables use `Modify run configuration` that can be found by right-clicking on the file.

![add environment variable](https://images.contentstack.io/v3/assets/blt39790b633ee0d5a7/blt8438bbb2ace0979e/64882bac83c7fb1a685f3d1b/Screenshot_2023-06-13_at_09.38.49.png)

And after everything done our final code would look like this

```kotlin
suspend fun setupConnection(
    databaseName: String = "sample_restaurants",
    connectionEnvVariable: String = "MONGODB_URI"
): MongoDatabase? {
    val connectString = if (System.getenv(connectionEnvVariable) != null) {
        System.getenv(connectionEnvVariable)
    } else {
        "mongodb+srv://<usename>:<password>@cluster0.sq3aiau.mongodb.net/?retryWrites=true&w=majority"
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
```

> An additional check for connectionString is added to demo, allowing you to use connection URI directly for ease, but it is strongly recommended
> to avoid hardcoding of connection URI.

With `setupConnection` function ready, let's test it, and query the database for collection count and name.

```kotlin
suspend fun listAllCollection(database: MongoDatabase) {

    val count = database.listCollectionNames().count()
    println("Collection count $count")

    print("Collection in this database are -----------> ")
    database.listCollectionNames().collect { print(" $it") }
}
```

And on running our output would look like this.

![list collection output](https://images.contentstack.io/v3/assets/blt39790b633ee0d5a7/blt5a670a8008abba48/648835185953929729a04668/Screenshot_2023-06-13_at_10.21.15.png)

And you would have noticed by now that we are using `suspend` keyword here with `listAllCollection()`. Since `listCollectionNames()` interact with
the database it is recommended to run it on a different thread and since MongoDB Kotlin driver supports the native Kotlin asynchronous language
paradigm i.e. [Coroutines](https://kotlinlang.org/docs/coroutines-guide.html) ,and we can benefit from it by using `suspend` functions.

Similarly, we can drop collections using `suspend` function.

```kotlin
suspend fun dropCollection(database: MongoDatabase) {
    database.getCollection<Objects>(collectionName = "test").drop()
}
```

With this complete, we are all set to start working on our CRUD application. So to start with, we need to create a `data` class that represents
restaurant information which would be saved into the database.

```kotlin
data class Restaurant(
    @BsonId
    val id: ObjectId,
    val address: Address,
    val borough: String,
    val cuisine: String,
    val grades: List<Grade>,
    val name: String,
    @BsonProperty("restaurant_id")
    val restaurantId: String
)

data class Address(
    val building: String,
    val street: String,
    val zipcode: String,
    val coord: List<Double>
)

data class Grade(
    val date: LocalDateTime,
    val grade: String,
    val score: Int
)
```

In the above code snippet, we used two annotations

1. `@BsonId` which are used to represent to the as unique identity `_id` of a document.
2. `@BsonProperty` which is used to create an alias for keys in the document like this case it represents `restaurant_id`.

> Also, to note here our `Restaurant` data class here is exact replicate of restaurant document, but we can skip few fields like `grades` or
`address` or make them optional while maintaining the ability to perform CRUD operation over it as MongoDB support flexible schema.

---------------------

## Create

With all the heavy lifting done earlier ( 10 lines of code for connecting), adding a new document to the database is really simple and just one
line of code with `insertOne`. So let's create a new file called `Create.kt`, which will contain all the create operations.

```kotlin
suspend fun addItem(database: MongoDatabase) {

    val collection = database.getCollection<Restaurant>(collectionName = "restaurants")
    val item = Restaurant(
        id = ObjectId(),
        address = Address(
            building = "Building", street = "street", zipcode = "zipcode", coord =
            listOf(Random.nextDouble(), Random.nextDouble())
        ),
        borough = "borough",
        cuisine = "cuisine",
        grades = listOf(
            Grade(
                date = LocalDateTime.now(),
                grade = "A",
                score = Random.nextInt()
            )
        ),
        name = "name",
        restaurantId = "restaurantId"
    )

    collection.insertOne(item).also {
        println("Item added with id - ${it.insertedId}")
    }
}
```

And the output on the console would be

![insert one](https://images.contentstack.io/v3/assets/blt39790b633ee0d5a7/blt1d124cbfb185d7d6/648ae0b2359ef0161360df47/Screenshot_2023-06-15_at_10.49.33.png)

> Don't forget to add environment variable again for this file, if you had trouble while running it.

If we want to add many documents to the collection we can use `insertMany`, which is recommended over running `insertOne` in a loop.

```kotlin
suspend fun addItems(database: MongoDatabase) {
    val collection = database.getCollection<Restaurant>(collectionName = "restaurants")
    val newRestaurants = collection.find<Restaurant>().first().run {
        listOf(
            this.copy(
                id = ObjectId(), name = "Insert Many Restaurant first", restaurantId = Random
                    .nextInt().toString()
            ),
            this.copy(
                id = ObjectId(), name = "Insert Many Restaurant second", restaurantId = Random
                    .nextInt().toString()
            )
        )
    }

    collection.insertMany(newRestaurants).also {
        println("Total items added ${it.insertedIds.size}")
    }
}

```

![Insert many output](https://images.contentstack.io/v3/assets/blt39790b633ee0d5a7/blt02fc3f33de844c88/648ae1ce2c4f87306c1b12ce/Screenshot_2023-06-15_at_11.02.48.png)

With these outputs on the console, we can say that data has been added successfully. But what if we want to see the object in the database one way
could be a Read operation, which we would do shortly or use [MongoDB compass](https://www.mongodb.com/products/compass) to view information.

![MongoDB compass](https://images.contentstack.io/v3/assets/blt39790b633ee0d5a7/blt72fd049dd230ea55/648ae40e1fb2d38f0e495940/Screenshot_2023-06-15_at_11.12.23.png)


---------------------

## Read

To read the information from the database, we can use the `find` operator and let's begin by reading any document.

```kotlin
val collection = database.getCollection<Restaurant>(collectionName = "restaurants")
collection.find<Restaurant>().limit(1).collect {
    println(it)
}
```

`find` operator returns a list of results but since we are only interested in a single document, we can use `limit` operator in conjunction to
limit our result set, in this case, it would be a single document.

If we extend this further and want to read a specific document, we can add filter parameters over the top of it like

```kotlin
val queryParams = Filters
    .and(
        listOf(
            eq("cuisine", "American"),
            eq("borough", "Queens")
        )
    )
```

Or any of the operators from this [list](https://www.mongodb.com/docs/manual/reference/operator/query/). And final code would look like this.

```kotlin
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
```

And our output would be something like this

![read specific doc output](https://images.contentstack.io/v3/assets/blt39790b633ee0d5a7/bltd837ac1a039ae43f/648ae83f0f2d9b551eed55e2/Screenshot_2023-06-15_at_11.30.20.png)

> Don't forget to add environment variable again for this file again, if you had trouble while running it.

Another practical use-case that comes with a read operation is how to add pagination to the results, this can be done with `limit` and
`offset` operator.

```kotlin
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
```

But with this approach, often query response time increase with value of `offset` and to overcome this we can benefit by creating a `Index` as
shown below.

```kotlin
val collection = database.getCollection<Restaurant>(collectionName = "restaurants")
val options = IndexOptions().apply {
    this.name("restaurant_id_index")
    this.background(true)
}

collection.createIndex(
    keys = Indexes.ascending("restaurant_id"),
    options = options
)
```

---------------------

## Update

Now let's discuss how to edit/update an existing document but again lets quickly create a new Kotlin file `Update.Kt`.

In general, there are two ways of updating any document(s) either

* Use the **update** operation, which allows us to update specific fields of the matching documents without impacting the other fields.
* Or **replace** operation which replaces the matching document with the new document.

For this exercise, we would be using the document we created earlier with create operation `{restaurant_id: "restaurantId"}`  update the
`restaurant_id` with a more realistic value. Let's split this into two sub-task for understanding  

First, using `Filters` we query to filter document, similar to Read operation earlier.

```kotlin
val collection = db.getCollection<Restaurant>("restaurants")
val queryParam = Filters.eq("restaurant_id", "restaurantId")
```

Then we can set the `restaurant_id` with a random integer value using `Updates`.

```kotlin
val updateParams = Updates.set("restaurant_id", Random.nextInt().toString())
```

And finally, we use `updateOne` to update the document in an atomic operation. 

```kotlin
collection.updateOne(filter = queryParam, update = updateParams).also {
    println("Total docs modified ${it.matchedCount} and fields modified ${it.modifiedCount}")
}
```

In the above example, we were already aware of which document we want to update, restaurant with an id `restauratantId` but there could few use-case
that might not be the case. In such cases we would first look up for the document and then update it, `findOneAndUpdate` can handy that allow you to
combine both in an atomic operation unlocking additional performance.

Another variation of the same could be updating multiple documents with one call and `updateMany` come in handy for such use cases. In our CRUD
app, we would update `cuisine` all restaurants to your favourite type and `borough` to Brooklyn.

```kotlin
suspend fun updateMultipleDocuments(db: MongoDatabase) {
    val collection = db.getCollection<Restaurant>("restaurants")
    val queryParam = Filters.eq(Restaurant::cuisine.name, "Chinese")
    val updateParams = Updates.combine(
        Updates.set(Restaurant::cuisine.name, "Indian"),
        Updates.set(Restaurant::borough.name, "Brooklyn")
    )

    collection.updateMany(filter = queryParam, update = updateParams).also {
        println("Total docs matched ${it.matchedCount} and modified ${it.modifiedCount}")
    }
}
```

In these examples, we used `set` and `combine` with `Updates` but there is much more to explore that allows us to do a more intuitive operation
like set currentDate or timestamp, increase or decrease the value of the field, etc. You can refer to the
[docs](https://mongodb.github.io/mongo-java-driver/4.9/apidocs/mongodb-driver-core/com/mongodb/client/model/Updates.html) for the complete list.

---------------------

## Delete

Now let's see the last operation of our CRUD app i.e. Delete and start with how to delete a single document and here we would be
using `findOneAndDelete` instead `deleteOne` which also returns the deleted document as output.

```kotlin
val collection = db.getCollection<Restaurant>(collectionName = "restaurants")
val queryParams = Filters.eq("restaurant_id", "restaurantId")

collection.findOneAndDelete(filter = queryParams).also {
    it?.let {
        println(it)
    }
}
```

![delete output](https://images.contentstack.io/v3/assets/blt39790b633ee0d5a7/blta4bb9c39c2356306/6489bf30352ac64eebda33c6/Screenshot_2023-06-14_at_14.21.37.png)

And to delete multiple documents we can use `deleteMany`, this would be handy to delete all the data we have created earlier with Create operation.

```kotlin
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
```

---------------------

## Summary

With this blog post, we have covered all the basic operations and have also seen how easily we can leverage the simplicity of Kotlin with the MongoDB
Kotlin driver. Thank you for reading and hopefully you find this article informative! The complete source code of the app can be found on
[GitHub][5].

If you have any queries or comments, you can share them on the [MongoDB forum](https://www.mongodb.com/community/forums/) or tweet
me [@codeWithMohit](https://twitter.com/codeWithMohit)

---------------------