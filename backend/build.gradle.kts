import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    java
    id("org.springframework.boot") version "3.5.0" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
}

val javaVersion = JavaVersion.VERSION_21
val lombokVersion = "1.18.36"
val vavrVersion = "0.10.4"
val springBootVersion = "3.5.0"

subprojects {
    apply(plugin = "java")
    apply(plugin = "io.spring.dependency-management")

    group = "ru.fuelup"
    version = "1.0.0"

    java {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }

    repositories {
        mavenCentral()
    }

    dependencyManagement {
        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:$springBootVersion")
        }
    }

    dependencies {
        compileOnly("org.projectlombok:lombok:$lombokVersion")
        annotationProcessor("org.projectlombok:lombok:$lombokVersion")
        testCompileOnly("org.projectlombok:lombok:$lombokVersion")
        testAnnotationProcessor("org.projectlombok:lombok:$lombokVersion")

        implementation("io.vavr:vavr:$vavrVersion")

        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    tasks.withType<BootJar> {
        enabled = false
    }
}

project(":infrastructure") {
    apply(plugin = "org.springframework.boot")

    dependencies {
        implementation(project(":common"))
        implementation(project(":customer:application"))
        implementation(project(":gasstation:application"))
        implementation(project(":order:application"))
        implementation(project(":loyalty:application"))
        implementation(project(":promocode:application"))

        implementation("org.springframework.boot:spring-boot-starter-web")
        implementation("org.springframework.boot:spring-boot-starter-security")
        implementation("org.springframework.boot:spring-boot-starter-data-jpa")
        implementation("org.springframework.boot:spring-boot-starter-data-redis")
        implementation("org.springframework.boot:spring-boot-starter-actuator")
        implementation("org.liquibase:liquibase-core")
        implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")
        implementation("io.micrometer:micrometer-registry-prometheus")
        implementation("com.github.loki4j:loki-logback-appender:1.5.2")
        runtimeOnly("org.postgresql:postgresql")
    }

    tasks.withType<BootJar> {
        enabled = true
        archiveFileName.set("fuelup-backend.jar")
    }
}
