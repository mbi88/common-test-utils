import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    id("ru.vyarus.quality").version("4.9.0")
    id("java-library")
    id("jacoco")
    id("maven-publish")
}

val suitesDir = "src/test/resources/suites/"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    api("com.github.mbi88", "json-assert", "master-SNAPSHOT", dependencyConfiguration = { isChanging = true })
    api("com.github.mbi88", "json-validator", "master-SNAPSHOT", dependencyConfiguration = { isChanging = true })
    api("com.github.mbi88", "http-request", "master-SNAPSHOT", dependencyConfiguration = { isChanging = true })
    api("com.github.mbi88", "date-handler", "master-SNAPSHOT", dependencyConfiguration = { isChanging = true })
    api("com.github.mbi88", "data-faker", "master-SNAPSHOT", dependencyConfiguration = { isChanging = true })
    api("org.testng:testng:7.7.1")
    api("org.json:json:20230227")
    api("io.rest-assured:rest-assured:5.3.0")
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
    implementation("com.amazonaws:aws-java-sdk-ssm:1.12.453")
    implementation("ch.qos.logback:logback-classic:1.4.6")
    implementation("com.google.guava:guava:31.1-jre")
    implementation("com.github.wnameless.json:json-flattener:0.16.4")
}

tasks.test {
    useTestNG {
        // Add test suites
        File(projectDir.absolutePath + "/" + suitesDir)
            .walk()
            .forEach {
                if (it.isFile) {
                    suites(it)
                }
            }

        testLogging {
            events("passed", "skipped", "failed")
            exceptionFormat = TestExceptionFormat.FULL
            showStandardStreams = true
        }
    }
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        html.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("${buildDir}/reports/coverage").get().asFile)
    }
}

java {
    withJavadocJar()
    withSourcesJar()
}

quality {
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
            groupId = "com.mbi"
            artifactId = "common-test-utils"
            version = "1.0"

            from(components["java"])
        }
    }
}
