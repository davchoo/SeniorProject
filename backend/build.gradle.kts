plugins {
    java
    id("org.springframework.boot") version "3.2.5"
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
    implementation("io.hypersistence:hypersistence-utils-hibernate-63:3.7.4")

    implementation("org.bouncycastle:bcpkix-jdk18on:1.78.1") // For Argon2

    val geotoolsVersion = "31.0"
    implementation("org.geotools:gt-wms:${geotoolsVersion}")
    implementation("org.geotools:gt-wfs-ng:${geotoolsVersion}")
    // Load CRSs from a properties file
    implementation("org.geotools:gt-epsg-wkt:${geotoolsVersion}")
    // Use a newer version of Guava, so IntelliJ doesn't complain about the CVEs from the old Guava version used by GeoTools
    implementation("com.google.guava:guava:33.0.0-jre")

    val netcdfJavaVersion = "5.5.3"
    implementation("edu.ucar:grib:${netcdfJavaVersion}") // Reading GRIB2 files
    implementation("edu.ucar:netcdf4:${netcdfJavaVersion}") // Reading NetCDF files

    implementation("com.google.maps:google-maps-services:2.2.0")

    runtimeOnly("org.postgresql:postgresql")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")

    testImplementation("io.zonky.test:embedded-database-spring-test:2.5.1")

    implementation("org.aspectj:aspectjrt")

    // Downloading files from AWS S3 bucket
    implementation(platform("software.amazon.awssdk:bom:2.25.21"))
    implementation("software.amazon.awssdk:s3")
    implementation("software.amazon.awssdk:s3-transfer-manager")
    implementation("software.amazon.awssdk.crt:aws-crt:0.29.14")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
