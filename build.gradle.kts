plugins {
    java
    application
}

repositories {
    jcenter()
}

sourceSets {
    val mainSourceSet = sourceSets.main.get().output

    create("sharedTest") {
        compileClasspath += mainSourceSet
        runtimeClasspath += mainSourceSet
    }

    create("intTest") {
        val testSourceSet = mainSourceSet + sourceSets["sharedTest"].output

        compileClasspath += testSourceSet
        runtimeClasspath += testSourceSet
    }

    create("e2eTest") {
        val testSourceSet = mainSourceSet + sourceSets["sharedTest"].output

        compileClasspath += testSourceSet
        runtimeClasspath += testSourceSet
    }
}

val sharedTestImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.implementation.get())
}

val intTestImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.implementation.get())
}

val e2eTestImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.implementation.get())
}

configurations["intTestRuntimeOnly"].extendsFrom(configurations["runtimeOnly"])
configurations["e2eTestRuntimeOnly"].extendsFrom(configurations["runtimeOnly"])

dependencies {
    implementation("org.igniterealtime.smack:smack:3.1.0")
    implementation("org.igniterealtime.smack:smackx:3.1.0")

    testImplementation("junit:junit:4.13")
    testImplementation("org.hamcrest:hamcrest-library:2.2")
    testImplementation("org.jmock:jmock-junit4:2.12.0")

    sharedTestImplementation("junit:junit:4.13")
    sharedTestImplementation("org.hamcrest:hamcrest-library:2.2")
    sharedTestImplementation("org.jmock:jmock-junit4:2.12.0")
    sharedTestImplementation("com.googlecode.windowlicker:windowlicker-swing:r268")

    intTestImplementation("junit:junit:4.13")
    intTestImplementation("org.hamcrest:hamcrest-library:2.2")
    intTestImplementation("org.jmock:jmock-junit4:2.12.0")
    intTestImplementation("com.googlecode.windowlicker:windowlicker-swing:r268")

    e2eTestImplementation("junit:junit:4.13")
    e2eTestImplementation("org.hamcrest:hamcrest-library:2.2")
    e2eTestImplementation("org.jmock:jmock-junit4:2.12.0")
    e2eTestImplementation("com.googlecode.windowlicker:windowlicker-swing:r268")
}

application {
    mainClassName = "auctionsniper.App"
}

var intTest = task<Test>("intTest") {
    description = "Runs integration tests."
    group = "verification"

    testClassesDirs = sourceSets["intTest"].output.classesDirs
    classpath = sourceSets["intTest"].runtimeClasspath
    shouldRunAfter("test")
}

var e2eTest = task<Test>("e2eTest") {
    description = "Runs end-to-end tests."
    group = "verification"

    testClassesDirs = sourceSets["e2eTest"].output.classesDirs
    classpath = sourceSets["e2eTest"].runtimeClasspath
    shouldRunAfter("intTest")
}

tasks.check { dependsOn(intTest) }
tasks.check { dependsOn(e2eTest) }
