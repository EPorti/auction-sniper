plugins {
    java
    application
}

repositories {
    jcenter()
}

sourceSets {
    create("e2eTest") {
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }
}

val e2eTestImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.implementation.get())
}

configurations["e2eTestRuntimeOnly"].extendsFrom(configurations["runtimeOnly"])

dependencies {
    implementation("org.igniterealtime.smack:smack:3.1.0")
    implementation("org.igniterealtime.smack:smackx:3.1.0")

    testImplementation("junit:junit:4.13")
    testImplementation("org.hamcrest:hamcrest-library:2.2")
    testImplementation("org.jmock:jmock-junit4:2.12.0")

    e2eTestImplementation("junit:junit:4.13")
    e2eTestImplementation("org.hamcrest:hamcrest-library:2.2")
    e2eTestImplementation("org.jmock:jmock-junit4:2.12.0")
    e2eTestImplementation("com.googlecode.windowlicker:windowlicker-swing:r268")
}

application {
    mainClassName = "auctionsniper.App"
}

var e2eTest = task<Test>("e2eTest") {
    description = "Runs end-to-end tests."
    group = "verification"

    testClassesDirs = sourceSets["e2eTest"].output.classesDirs
    classpath = sourceSets["e2eTest"].runtimeClasspath
    shouldRunAfter("test")
}

tasks.check { dependsOn(e2eTest) }
