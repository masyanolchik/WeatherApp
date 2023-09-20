buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0")
        classpath("com.codingfeline.buildkonfig:buildkonfig-gradle-plugin:0.14.0")
    }
}

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    kotlin("multiplatform").apply(false)
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.10" apply false
    id("app.cash.sqldelight") version "2.0.0-alpha05" apply false
    id("com.android.application").apply(false)
    id("com.android.library").apply(false)
    id("org.jetbrains.compose") version "1.5.10-beta01" apply false
    id("com.codingfeline.buildkonfig") version "0.14.0" apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}