val kotlinVersion = fromExtra("kotlinVersion")!!


dependencies {
    implementation("com.google.code.gson:gson:2.8.6")
    implementation(kotlin("stdlib", kotlinVersion))
    implementation(project(":archiver"))

    testImplementation("junit:junit:4.12")
}

application {
   mainClass.set("com.kochetkov.archiver.app.Main")
}

tasks.register<Jar>("archiver-lab2-fatJar") {
    dependsOn(tasks.build, tasks.test)

    archiveVersion.set("")
    archiveFileName.set("archiver.jar")
    archiveClassifier.set("archiver")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes(
            "Main-Class" to "com.kochetkov.archiver.application.Main",
            "Multi-Release" to "true"
        )
    }

    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })

    val sourcesMain = sourceSets.main.get()
    from(sourcesMain.output)
}

fun fromExtra(parameter: String): String? {
    return project.rootProject.ext.properties[parameter] as String?
}
