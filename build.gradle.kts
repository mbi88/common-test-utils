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
    api("com.github.mbi88:json-assert:1.0.2")
    api("com.github.mbi88:json-validator:1.1.4")
    api("com.github.mbi88:http-request:1.6.4")
    api("com.github.mbi88:date-handler:1.0.3")
    api("com.github.mbi88:data-faker:1.1.11")
    api("org.testng:testng:7.11.0")
    api("org.json:json:20250517")
    api("io.rest-assured:rest-assured:5.5.6")
    api("joda-time:joda-time:2.14.0")
    implementation("io.jsonwebtoken:jjwt-api:0.13.0")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.13.0")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.13.0")
    implementation("software.amazon.awssdk:ssm:2.39.0")
    implementation("ch.qos.logback:logback-classic:1.5.19")
    implementation("com.google.guava:guava:33.5.0-jre")
    implementation("com.github.wnameless.json:json-flattener:0.17.3")
    testImplementation("org.mockito:mockito-core:5.20.0")
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

tasks.withType<JavaCompile> {
    options.compilerArgs.add("--enable-preview")
}

tasks.withType<JavaExec> {
    jvmArgs("--enable-preview")
}

tasks.withType<Javadoc> {
    (options as StandardJavadocDocletOptions).addBooleanOption("Xdoclint:none", true)
    (options as StandardJavadocDocletOptions).addBooleanOption("-enable-preview", true)
    (options as StandardJavadocDocletOptions).addStringOption("-release", "21")
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
