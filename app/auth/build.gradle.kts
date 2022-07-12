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
    implementation(project(":app:email"))

    implementation(kotlin("stdlib"))

    // Spring
    implementation("org.springframework.security:spring-security-web")
    implementation("org.springframework.security:spring-security-config")

    // Servlet
    implementation("org.apache.tomcat.embed:tomcat-embed-core")

    // Jwt
    implementation(libs.jsonwebtoken.jjwt)
    implementation(libs.bundles.mybatis)
}

tasks.getByName<BootJar>("bootJar") {
    enabled = false
}