import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinVersion = "1.3.70"
val spekVersion = "2.0.10"
val coroutinesVersion = "1.3.4"
val serializationVersion = "0.20.0"

group = "poker"
version = "1.0-SNAPSHOT"

buildscript {
  val kotlinVersion = "1.3.70"
  repositories { jcenter() }

  dependencies {
    classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    classpath ("org.jetbrains.kotlin:kotlin-serialization:$kotlinVersion")
  }
}

plugins {
  val kotlinVersion = "1.3.70"
  kotlin("jvm") version kotlinVersion
  kotlin("plugin.serialization")  version kotlinVersion
}

tasks.withType(KotlinCompile::class).all {
  kotlinOptions {
    jvmTarget = "13"
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
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:$serializationVersion")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:$serializationVersion")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
  testImplementation(kotlin("test", kotlinVersion))
  testImplementation("org.spekframework.spek2:spek-dsl-jvm:${spekVersion}")
  testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:${spekVersion}")
}
