import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.0"
}

group = "io.github.dimonchik0036.converter.novel"
version = "0.1"
val fuelVersion = "1.16.0"
repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    compile("com.github.kittinunf.fuel:fuel:$fuelVersion")
    compile("com.github.kittinunf.fuel:fuel-coroutines:$fuelVersion")
    compile("com.xenomachina:kotlin-argparser:2.0.7")
    compile(kotlin("stdlib-jdk8"))
}

task<Jar>("fatJar") {
    baseName = "${project.name}-fatJar"
    manifest {
        attributes(
            mapOf(
                "Implementation-Version" to project.version,
                "Main-Class" to "${project.group}.ApplicationKt"
            )
        )
    }

    from(configurations.runtime.map { if (it.isDirectory) it else zipTree(it) })
    with(tasks["jar"] as CopySpec)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}