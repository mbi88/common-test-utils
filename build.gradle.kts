import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    id("ru.vyarus.quality").version("5.0.0")
    id("java-library")
    id("jacoco")
    id("maven-publish")
}

group = "com.mbi"
version = "1.0"

val suitesDir = "src/test/resources/suites/"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    api("com.github.mbi88:json-assert:37e11468be")
    api("com.github.mbi88:json-validator:2479f9ebd6")
    api("com.github.mbi88:http-request:8c9e0067ae")
    api("com.github.mbi88:date-handler:f1f3281b31")
    api("com.github.mbi88:data-faker:dba50b30e5")
    api("org.testng:testng:7.11.0")
    api("org.json:json:20250107")
    api("io.rest-assured:rest-assured:5.5.1")
    api("joda-time:joda-time:2.13.1")
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")
    implementation("software.amazon.awssdk:ssm:2.31.4")
    implementation("ch.qos.logback:logback-classic:1.5.18")
    implementation("com.google.guava:guava:33.4.5-jre")
    implementation("com.github.wnameless.json:json-flattener:0.17.2")
    testImplementation("org.mockito:mockito-core:5.16.1")
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("--enable-preview")
}

tasks.withType<JavaExec> {
    jvmArgs("--enable-preview")
}

tasks.withType<Javadoc> {
    val opts = options as StandardJavadocDocletOptions
    opts.addBooleanOption("Xdoclint:none", true)
    opts.addBooleanOption("-enable-preview", true)
    opts.addStringOption("-release", "21")
}

tasks.test {
    jvmArgs("--enable-preview")

    useTestNG {
        // Automatically include all XML test suite files from suitesDir
        fileTree(suitesDir).matching { include("*.xml") }.files.forEach { suites(it) }
    }

    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = TestExceptionFormat.FULL
        showStandardStreams = true
    }
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        html.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("reports/coverage"))
    }
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks.withType<Javadoc> {
    (options as StandardJavadocDocletOptions).addBooleanOption("Xdoclint:none", true)
}

quality {
    // Enable all supported static analysis tools
    checkstyleVersion = "10.16.0"
    checkstyle = true
    pmd = true
    codenarc = true
    spotbugs = true
}

tasks.check {
    dependsOn(tasks.jacocoTestReport)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "common-test-utils"
            from(components["java"])
        }
    }
}
