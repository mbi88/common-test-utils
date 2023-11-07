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
    api("org.testng:testng:7.8.0")
    api("org.json:json:20231013")
    api("io.rest-assured:rest-assured:5.3.2")
    api("joda-time:joda-time:2.12.5")
    implementation("io.jsonwebtoken:jjwt-api:0.12.3")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.3")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.3")
    implementation("software.amazon.awssdk:ssm:2.21.16")
    implementation("ch.qos.logback:logback-classic:1.4.11")
    implementation("com.google.guava:guava:32.1.3-jre")
    implementation("com.github.wnameless.json:json-flattener:0.16.6")
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

tasks.withType<Javadoc> {
    val opts = options as StandardJavadocDocletOptions
    opts.addBooleanOption("Xdoclint:none", true)
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
