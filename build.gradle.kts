import com.github.spotbugs.snom.Effort
import com.github.spotbugs.snom.SpotBugsReport
import com.github.spotbugs.snom.SpotBugsTask
import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    id("java-library")
    id("jacoco")
    id("maven-publish")
    id("code-quality")
    id("com.github.spotbugs") version "6.4.8"
}

group = "com.mbi"
version = "1.0"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    api("com.github.mbi88:json-assert:1.0.3")
    api("com.github.mbi88:json-validator:1.1.6")
    api("com.github.mbi88:http-request:1.6.5")
    api("com.github.mbi88:date-handler:1.0.4")
    api("com.github.mbi88:data-faker:1.1.12")
    api("org.testng:testng:7.12.0")
    api("io.rest-assured:rest-assured:6.0.0")
    api("org.json:json:20251224")
    api("joda-time:joda-time:2.14.0")
    implementation("io.jsonwebtoken:jjwt-api:0.13.0")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.13.0")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.13.0")
    implementation("software.amazon.awssdk:ssm:2.41.12")
    implementation("ch.qos.logback:logback-classic:1.5.25")
    implementation("com.google.guava:guava:33.5.0-jre")
    implementation("com.github.wnameless.json:json-flattener:0.18.0")
    testImplementation("org.mockito:mockito-core:5.21.0")
}

tasks.test {
    jvmArgs(
        "--enable-preview",
        "-XX:+EnableDynamicAgentLoading"
    )

    useTestNG {
        // Automatically include all XML test suite files from suitesDir
        val suitesDir = "src/test/resources/suites/"
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

spotbugs {
    toolVersion.set("4.9.3")
    effort.set(Effort.MAX)
    excludeFilter.set(file("config/spotbugs/excludeFilter.xml"))
}

tasks.withType<SpotBugsTask>().configureEach {
    val taskName = name
    val html = reports.maybeCreate("html") as SpotBugsReport
    html.required.set(true)
    html.outputLocation.set(layout.buildDirectory.file("reports/spotbugs/$taskName.html"))
}

tasks {
    named("checkstyleTest") { enabled = false }
    named("pmdTest") { enabled = false }
    named("spotbugsTest") { enabled = false }
}

tasks.check {
    dependsOn(
        tasks.jacocoTestReport,
        tasks.checkstyleMain,
        tasks.pmdMain,
        tasks.spotbugsMain
    )
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "common-test-utils"
            from(components["java"])
        }
    }
}
