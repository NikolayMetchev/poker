import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinVersion = "1.3.61"
val spekVersion = "2.0.8"
val coroutinesVersion = "1.3.2"

group = "poker"
version = "1.0-SNAPSHOT"

plugins {
  kotlin("jvm") version "1.3.61"
}

tasks.withType(KotlinCompile::class).all {
  kotlinOptions {
    jvmTarget = "12"
  }
}

val test by tasks.getting(Test::class) {
  useJUnitPlatform {
    includeEngines("spek2")
  }
}

repositories {
  jcenter()
}

@Suppress("SpellCheckingInspection")
dependencies {
  implementation(kotlin("stdlib-jdk8", kotlinVersion))
  implementation(kotlin("reflect", kotlinVersion))
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
  testImplementation(kotlin("test", kotlinVersion))
  testImplementation("org.spekframework.spek2:spek-dsl-jvm:${spekVersion}")
  testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:${spekVersion}")
}
