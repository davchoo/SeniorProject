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
    implementation("com.google.maps:google-maps-services:2.2.0")
    //implementation("org.slf4j:slf4j-simple:2.0.5")
//    implementation("com.google.appengine:appengine-api-1.0-sdk:2.2.0")
//    implementation("org.bouncycastle:bcpkix-jdk15on") // For Argon2

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
