plugins {
    `jacoco-report-aggregation`
}

repositories {
    mavenCentral()
}

dependencies {
    jacocoAggregation(project(":api"))
    jacocoAggregation(project(":lib"))
    jacocoAggregation(project(":test"))
    jacocoAggregation(project(":jna"))
    jacocoAggregation(project(":ffm"))
    jacocoAggregation(project(":regex"))
}

reporting {
    reports {
        register<JacocoCoverageReport>("jacocoAggregatedTestReport") {
            testType = TestSuiteType.UNIT_TEST
        }
    }
}
