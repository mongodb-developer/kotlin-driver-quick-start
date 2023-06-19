plugins {
    id("org.jetbrains.kotlin.jvm") version "1.8.0"
    id("application")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.mongodb:mongodb-driver-kotlin-coroutine:4.10.0-SNAPSHOT")
    implementation("org.slf4j:slf4j-simple:1.7.9")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}

application {
    mainClassName = "SetupKt"
}
