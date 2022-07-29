import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    id("ru.vyarus.quality").version("4.7.0")
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
    api("org.testng:testng:7.6.1")
    api("org.json:json:20220320")
    api("io.rest-assured:rest-assured:5.1.1")
    implementation("io.jsonwebtoken:jjwt:0.9.1") {
        implementation("javax.xml.bind:jaxb-api:2.3.1")
    }
    implementation("com.amazonaws:aws-java-sdk-ssm:1.12.271")
    implementation("ch.qos.logback:logback-classic:1.2.11")
    implementation("com.google.guava:guava:31.1-jre")
    implementation("com.github.wnameless.json:json-flattener:0.13.0")
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
