import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.spring") version "1.6.10"
    id("org.springframework.boot") version "2.7.0"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":app:common"))
    implementation(project(":app:auth"))

    // Kotlin
    implementation(kotlin("stdlib"))

    // Spring
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.security:spring-security-config")

    // Libs
    implementation(libs.mysql.connector)
    implementation(libs.bundles.mybatis)
    implementation(libs.bundles.springdoc)
}

tasks.getByName<BootJar>("bootJar") {
    enabled = false
}