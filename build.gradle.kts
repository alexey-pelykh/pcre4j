plugins {
    base
    `jacoco-report-aggregation`
    id("org.jreleaser") version "1.22.0"
}

version = findProperty("pcre4j.version") as String? ?: "0.0.0-SNAPSHOT"

jreleaser {
    configFile = file("jreleaser.yml")
}

repositories {
    mavenCentral()
}

dependencies {
    jacocoAggregation(project(":api"))
    jacocoAggregation(project(":lib"))
    jacocoAggregation(project(":jna"))
    jacocoAggregation(project(":ffm"))
    jacocoAggregation(project(":regex"))
}

reporting {
    reports {
        register<JacocoCoverageReport>("jacocoAggregatedTestReport") {
            testSuiteName = "test"
        }
    }
}
