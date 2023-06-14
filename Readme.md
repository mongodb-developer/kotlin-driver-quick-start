[1]: https://www.mongodb.com/atlas/database

[2]: https://www.mongodb.com/docs/atlas/sample-data/

[3]: https://www.mongodb.com/cloud/atlas/register

[4]: https://www.mongodb.com/docs/drivers/kotlin/coroutine/current/quick-start/

# Getting started with MongoDB for building backend Application using official Kotlin driver

> This is an introduction article on how to build backend application in Kotlin using official MongoDB Kotlin driver and [MongoDB Atlas][1].

## Prerequisites

This is a getting-started article, therefore nothing much is needed as a prerequisite, but familiarity with Kotlin as a programming language would be
helpful.

Also, we need an [Atlas account][3], which is free forever. Create an account if you haven't got one, that provide MongoDB as a cloud database and
much more.

In general, MongoDB is an open-source, cross-platform, and distributed document database that allows building apps with flexible schema. In
case, you are not familiar or would like a quick recap I would recommend exploring the MongoDB Jumpstart series to get familiar with MongoDB and its
various services in under 10 minutes or if you prefer to read then you can follow this [guide](https://www.mongodb.com/docs/atlas/getting-started/).

And last to aid our development activities, we would be using [Jetbrains IntelliJ IDEA (Community Edition)](https://www.jetbrains.com/idea/download/).

## Kotlin driver vs Realm Kotlin SDK ?

Before we start I would like touch base upon [Realm Kotlin SDK](https://www.mongodb.com/docs/realm/sdk/kotlin/), which another framework by MongoDB
in Kotlin that is used to create client application using MongoDB ecosystem and shouldn't be confused with this. [MongoDB Kotlin driver][4] is used
to create backend application like RESTful application which can be consumed by client app's.

To make learning more meaning and practical we would be building a CRUD application, feel free to check out our
[Github repo](https://github.com/mongodb-developer/kotlin-driver-quick-start) if you would like to follow along together. So Without further ado let's
get started. 

## Create a project

We can create the project using the Project wizard from using File menu, followed by New and then Project. From the wizard screen change the
language to Kotlin as we would be using Kotlin as programming language.

![Project wizard](https://images.contentstack.io/v3/assets/blt39790b633ee0d5a7/bltbce8e7adda583e3e/648793838b23a5b3d5052b69/Screenshot_2023-06-12_at_09.34.09.png)

After initial gradle sync and dependency download our project is ready to run, so lets give it a try using the run icon in the
menu bar or simply press CTRL + R for mac. For now our project wouldn't print or do anything as just started but our you should see `BUILD
SUCCESSFUL` in the run console as showing below

![build success](https://images.contentstack.io/v3/assets/blt39790b633ee0d5a7/blt97a67a3d4a402196/64879383d40ad08ec16808a9/Screenshot_2023-06-12_at_13.42.38.png)

Now let's add Kotlin driver to our project that would allow us to interact with [MongoDB Atlas][1].

## Adding MongoDB Kotlin driver

Adding driver to the project is simple and straight forward, just update the `dependencies` block in build file i.e. `build.gradle` with these.

```groovy
dependencies {
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4'
    implementation 'org.mongodb:mongodb-driver-kotlin-coroutine:4.10.0-SNAPSHOT'
}
```

With this done we can now connect with [MongoDB Atlas][1] using Kotlin driver.

## Connecting to database

To connect with the database, we first need the `Connection URI` that can be found on pressing connect to cluster at
[Atlas account][3], as shown below.

![image](https://images.contentstack.io/v3/assets/blt39790b633ee0d5a7/blt1d92c6f1c6654b04/648793839625e14516b3657c/68747470733a2f2f6d6f6e676f64622d6465766875622d636d732e73332e75732d776573742d312e616d617a6f6e6177732e636f6d2f436f6e6e656374696f6e5f5552495f666439393037653262642e706e67.png)

For details, you can also refer to this [documentation](https://www.mongodb.com/docs/guides/atlas/connection-string/).

Now we create a Kotlin file,`Setup.kt` with a `main` function like this

![Setup.kt file](https://images.contentstack.io/v3/assets/blt39790b633ee0d5a7/bltdc533d1983ce2f87/6488261e8b23a52669052cee/Screenshot_2023-06-13_at_09.17.29.png)

In order to create database object, we need to first create MongoClient instance using `Connection URI` from above

```kotlin
val connectionString = "mongodb+srv://mohitsharma:<enter your password>@cluster0.sq3aiau.mongodb.net/?retryWrites=true&w=majority"
val client = MongoClient.create(connectionString = connectString)
```

With this client object, we create database instance with collection name, `sample_restaurants` which is restaurant sample dataset available as
[sample dataset][2] in Atlas, that will also help us build more realistic application.

```kotlin
val databaseName = "sample_restaurants"
val db: MongoDatabase = client.getDatabase(databaseName = databaseName)
```

And since it's not good practice to hardcode `connectionString` in code, lets move it into the environment variable using `Modify run configuration`
by right-clicking the file.

![add environment variable](https://images.contentstack.io/v3/assets/blt39790b633ee0d5a7/blt8438bbb2ace0979e/64882bac83c7fb1a685f3d1b/Screenshot_2023-06-13_at_09.38.49.png)

And with all change our connection function look this

```kotlin
fun setupConnection(
    databaseName: String = "sample_restaurants",
    connectionEnvVariable: String = "MONGODB_URI"
):
        MongoDatabase {
    val connectString = if (System.getenv(connectionEnvVariable) != null) {
        System.getenv(connectionEnvVariable)
    } else {
        "mongodb+srv://<username>:<enter your password>@cluster0.sq3aiau.mongodb" +
                ".net/?retryWrites=true&w=majority"
    }
    val client = MongoClient.create(connectionString = connectString)
    return client.getDatabase(databaseName = databaseName)
}
```

> An additional check for connectionString is added to demo, allowing you to use connection URI directly for ease, but it is strongly recommended
> pass connection URI as an environment variable.

With `setupConnection` function ready, we can query the database for collection count and name.

```kotlin
suspend fun listAllCollection(database: MongoDatabase) {

    val count = database.listCollectionNames().count()
    println("Collection count $count")

    print("Collection in this database are -----------> ")
    database.listCollectionNames().collect { print(" $it") }
}
```

![list collection output](https://images.contentstack.io/v3/assets/blt39790b633ee0d5a7/blt5a670a8008abba48/648835185953929729a04668/Screenshot_2023-06-13_at_10.21.15.png)

And you would have noticed by now that we are using `suspend` keyword here, as `listCollectionNames()` is an asynchronous function, allowing us to
take advantage of Kotlin language native paradigm of asynchronous programming.

Similarly, we can create or drop collections using `suspend` function.

```kotlin
suspend fun createCollection(database: MongoDatabase) {
    database.createCollection(
        collectionName = "test",
        createCollectionOptions = CreateCollectionOptions().maxDocuments(100)
    )
}
```

```kotlin
suspend fun dropCollection(database: MongoDatabase) {
    database.getCollection<Objects>(collectionName = "test").drop()
}
```

With this complete, we are all set to start working on our CRUD application. So to start with, we need to create a `data` class that represent a
restaurant information which we want save into the database.

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
    val date: Date,
    val grade: String,
    val score: Int
)
```

In the above code snippet we used two annotation

1. `@BsonId` which is used to represent to as unique identity `_id` of a document.
2. `@BsonProperty` which is used to create alias for key in the document like this case it represent `restaurant_id`.

Also, to note here our `Restaurant` data class here is exact replicate of restaurant document, but we can skip few fields like `grades` or
`address` while maintaining the ability to perform CRUD operation over it as MongoDB support flexible schema.

## Create

With all the heavy lifting done earlier (5 lines of code for connecting), adding new document to database is really simple and just one line of
code with `insertOne`. So let's create a new file called `Create.kt`, which will contain all the create operations.

```kotlin
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
            date = Date(System.currentTimeMillis()),
            grade = "A",
            score = Random.nextInt()
        )
    ),
    name = "name",
    restaurantId = "restaurantId"
)

collection.insertOne(item).also {
    println(it)
}
```

If we want to add many documents into the collection we can use `insertMany`, which is preferred against `insertOne` running over the loop.

```kotlin
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
    println("Insert Many $it")
}
```

## Read

Similarly, we can read the information with `find` operator as well. Let's start with reading a random document

```kotlin
val collection = database.getCollection<Restaurant>(collectionName = "restaurants")
collection.find<Restaurant>().limit(1).collect {
    println(it)
}
```

and if we want to read a specific document, we can add filter parameters.

```kotlin
val queryParams = Filters
    .and(
        listOf(
            eq("cuisine", "American"),
            eq("borough", "Queens")
        )
    )
```

or any of the operator from this huge [list](https://www.mongodb.com/docs/manual/reference/operator/query/) and together with it our code look
like this.

```kotlin
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
    .colect { println(it) }
```

Another practical use-case that come with read operation is how to add pagination to the results, this can be done with `limit` and
`offset` operator.

```kotlin
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

## Update

Let's discuss how to edit/update an existing document but before that lets quickly create a new Kotlin file `Update.Kt` which will have all the
update related code.

In general there are two ways of updating any document(s) either

* Use **update** operation, which allows us to update specific fields of the matching documents without impacting the other fields.
* Or **replace** operation which replace matching document with the new document.

For this exercise we would use the document we created earlier with create operation `{restaurant_id: "restaurantId"}` and update the
`restaurant_id` with a more realistic value.

First we need to query the document using `Filters`, similar to Read operation earlier.

```kotlin
val collection = db.getCollection<Restaurant>("restaurants")
val queryParam = Filters.eq("restaurant_id", "restaurantId")
```

Then we can set the `restaurant_id` with a random integer value using `Updates`.

```kotlin
val updateParams = Updates.set("restaurant_id", Random.nextInt().toString())
```

And finally we use `updateOne` to update the document.

```kotlin
collection.updateOne(filter = queryParam, update = updateParams).also {
    println("Total docs modified ${it.matchedCount} and fields modified ${it.modifiedCount}")
}
```

In the above example, we were already aware which document we had to want to update, restaurant with id `restauratantId` but in real world app
that might be not be the case. So then you would first look for the document then update it (two operation for one task) which can lead to
performance issue or race condition.

To overcome this, we could use `findOneAndUpdate` that allow you to combine both in an atomic operation.

Another variation of the same could be updating multiple documents with one call and such cases we have `updateMany`. In our CRUD app, we would  
update `cuisine` for all restaurants to your favourite type and `borough` to Brooklyn.

```kotlin
val collection = db.getCollection<Restaurant>("restaurants")
val queryParam = Filters.eq(Restaurant::cuisine.name, "Chinese")
val updateParams = Updates.combine(
    Updates.set(Restaurant::cuisine.name, "Indian"),
    Updates.set(Restaurant::borough.name, "Brooklyn")
)

collection.updateMany(filter = queryParam, update = updateParams).also {
    println("Total docs matched ${it.matchedCount} and modified ${it.modifiedCount}")
}
```

In these examples we used `set` and `combine` with `Updates` but there is much more to explore that allows us to do more intuitive operation like set
currentDate or timestamp, increase or decease the value of the field ,etc. You can refer to the
[docs](https://mongodb.github.io/mongo-java-driver/4.9/apidocs/mongodb-driver-core/com/mongodb/client/model/Updates.html) for the complete list.

## Delete

Now lets see the last operation of our CRUD app i.e. Delete. So lets start with how to delete a single document and here we would be
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

## Summary 



 






 

