plugins {
    java
    id("org.springframework.boot") version "3.2.2"
    id("io.spring.dependency-management") version "1.1.4"
}

group = "team.travel"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
    maven {
        url = uri("https://repo.osgeo.org/repository/release/")
        mavenContent {
            releasesOnly()
        }
    }

    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.security:spring-security-crypto")
    implementation("org.springframework.session:spring-session-core")
    implementation("org.springframework.session:spring-session-jdbc")
    implementation("org.hibernate.orm:hibernate-spatial")

    val geotoolsVersion = "30.2"
    implementation("org.geotools:gt-wms:${geotoolsVersion}")
    implementation("org.geotools:gt-wfs-ng:${geotoolsVersion}")
    // Load CRSs from a properties file
    implementation("org.geotools:gt-epsg-wkt:${geotoolsVersion}")
    // Use a newer version of Guava, so IntelliJ doesn't complain about the CVEs from the old Guava version used by GeoTools
    implementation("com.google.guava:guava:33.0.0-jre")

    implementation("com.google.maps:google-maps-services:2.2.0")

    runtimeOnly("org.postgresql:postgresql")

    developmentOnly("org.springframework.boot:spring-boot-devtools")
    developmentOnly("com.h2database:h2")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("com.h2database:h2")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
