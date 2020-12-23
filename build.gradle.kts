import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
    application
}

group = "io.futz.cdktf"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    // cdktf only publishes artifacts to GitHub packages so this repository is required
    // GITHUB_CDKTF_SECRET is a GitHub personal token capable of reading GitHub packages.
    maven("https://maven.pkg.github.com/hashicorp/terraform-cdk") {
        credentials {
            username = System.getenv("GITHUB_USERNAME")
            password = System.getenv("GITHUB_CDKTF_SECRET")
        }
    }
}

dependencies {
    implementation("com.hashicorp:cdktf:0.0.19")
    implementation("software.constructs:constructs:5.0.0")
    testImplementation(kotlin("test-junit"))
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClassName = "io.futz.cdktf.MainKt"
}
