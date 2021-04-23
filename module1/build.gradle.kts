/*
 * This file was generated by the Gradle 'init' task.
 *
 * This is a general purpose Gradle build.
 * Learn how to create Gradle builds at https://guides.gradle.org/creating-new-gradle-builds
 */

plugins {
    application
    kotlin("plugin.serialization") version "1.4.0"
    kotlin("jvm") version "1.4.0"
    id("com.google.cloud.tools.appengine") version "2.4.1"
    id("com.github.johnrengelman.shadow") version "6.0.0"
}

repositories {
    mavenCentral()
}

configure<com.google.cloud.tools.gradle.appengine.appyaml.AppEngineAppYamlExtension> {
    val project = System.getenv("GCP_PROJECT")

    deploy {
        projectId = project
        version = "4-2-0"
    }
}

tasks {
    build {
        dependsOn(shadowJar)
    }
    val shadowJarTask = named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        excludes.add("*.js")
        // explicitly configure the filename of the resulting UberJar
        archiveFileName.set("screw-you.jar")

        // Appends entries in META-INF/services resources into a single resource. For example, if there are several
        // META-INF/services/org.apache.maven.project.ProjectBuilder resources spread across many JARs the individual
        // entries will all be concatenated into a single META-INF/services/org.apache.maven.project.ProjectBuilder
        // resource packaged into the resultant JAR produced by the shading process -
        // Effectively ensures we bring along all the necessary bits from Jetty
        mergeServiceFiles()

        // As per the App Engine java11 standard environment requirements listed here:
        // https://cloud.google.com/appengine/docs/standard/java11/runtime
        // Your Jar must contain a Main-Class entry in its META-INF/MANIFEST.MF metadata file
        manifest {
            attributes(mapOf("Main-Class" to application.mainClassName))
        }
    }
    // because we're using shadowJar, this task has limited value
    named("jar") {
        enabled = false
    }
    // update the `assemble` task to ensure the creation of a brand new UberJar using the shadowJar task
    named("assemble") {
        dependsOn(shadowJarTask)
    }
}


kotlin {
    sourceSets {
       main {
       }
        test {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("org.junit.jupiter:junit-jupiter:5.4.2")
                implementation("org.assertj:assertj-core:3.15.0")
                implementation("io.mockk:mockk:1.10.2")
            }
        }
    }
}