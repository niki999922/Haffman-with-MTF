plugins {
    kotlin("jvm") version "1.5.0"
    application
}

val version by extra("0.1.0")
val kotlinVersion by extra("1.5.0")


allprojects {
    apply(plugin = "kotlin")
    apply(plugin = "application")


    group = "com.kochetkov.archiver"
    version = version

    repositories {
        mavenCentral()
        jcenter()
    }

    kotlin {
        this.sourceSets {
            main {
                kotlin.srcDirs("src/main/java")
                kotlin.srcDirs("src/main/kotlin")
                resources.srcDirs("src/main/resources")
            }

            test {
                kotlin.srcDirs("src/test/java")
                kotlin.srcDirs("src/test/kotlin")
                resources.srcDirs("src/test/resources")
            }
        }

        version = kotlinVersion
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    tasks.test {
        useJUnit()

        maxParallelForks = 4
        maxHeapSize = "2G"
        reports.html.isEnabled = false
    }
}