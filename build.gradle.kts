import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    application
}

group = "jun.won.lee"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    val version = "4.6.3"
    testImplementation("io.kotest:kotest-runner-junit5-jvm:$version")
    testImplementation("io.kotest:kotest-runner-junit5:$version")
    testImplementation("io.kotest:kotest-assertions-core:$version")
    testImplementation("io.kotest:kotest-property:$version")
    testImplementation("io.kotest:kotest-assertions-compiler:$version")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}