import java.io.File
import java.io.FileInputStream
import java.util.*

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.android.library")
    id("org.jetbrains.compose")
    id("app.cash.sqldelight")
    id("com.codingfeline.buildkonfig")
}


buildkonfig {
    packageName = "com.weatherapp.core"
    val prop = Properties().apply {
        load(FileInputStream(File(rootProject.rootDir, "local.properties")))
    }.getProperty("WEATHER_API_KEY")
    defaultConfigs {
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "API_KEY", prop)
    }
}

kotlin {
    val ktorVersion = "2.2.3"
    val coroutinesVersion = "1.7.1"
    val sqlDelightVersion = "2.0.0-alpha05"
    val koinVersion = "3.2.0"
    val mviKotlinVersion = "3.1.0"
    val kotlinDecomposeVersion = "1.0.0-compose-experimental"
    val dateTimeVersion = "0.4.1"

    androidTarget()
    jvm("desktop")

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions.jvmTarget = "11"
    }

    kotlin {
        jvmToolchain {
            (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(11))
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)

                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
                // Kotlin Serialization
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
                // Ktor
                implementation("io.ktor:ktor-client-core:${ktorVersion}")
                implementation("io.ktor:ktor-serialization-kotlinx-json:${ktorVersion}")
                implementation("io.ktor:ktor-client-content-negotiation:${ktorVersion}")
                implementation("io.ktor:ktor-client-logging:${ktorVersion}")
                // SqlDelight
                implementation("app.cash.sqldelight:coroutines-extensions:${sqlDelightVersion}")
                implementation("app.cash.sqldelight:primitive-adapters:${sqlDelightVersion}")

                // Koin
                implementation("io.insert-koin:koin-core:$koinVersion")
                implementation("io.insert-koin:koin-test:$koinVersion")

                // MVIKotlin
                implementation("com.arkivanov.mvikotlin:mvikotlin:$mviKotlinVersion")
                implementation("com.arkivanov.mvikotlin:mvikotlin-main:$mviKotlinVersion")
                implementation("com.arkivanov.mvikotlin:mvikotlin-extensions-coroutines:$mviKotlinVersion")

                //Coroutines
                implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")

                //Decompose
                implementation("com.arkivanov.decompose:decompose:$kotlinDecomposeVersion")
                implementation("com.arkivanov.decompose:extensions-compose-jetbrains:$kotlinDecomposeVersion")

                // Async Image Loader
                implementation("io.github.qdsfdhvh:image-loader:1.2.10")

                // Date Time library
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:$dateTimeVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                //implementation(kotlin("test"))
                //implementation(kotlin("test-common"))
                //implementation(kotlin("test-annotations-common"))
                implementation("org.junit.jupiter:junit-jupiter:5.8.2")

                // kotest
                implementation("io.kotest:kotest-runner-junit5:5.7.2")
                implementation("io.kotest:kotest-assertions-core:5.7.2")
                implementation("io.kotest:kotest-property:5.7.2")
                implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")
                implementation("io.ktor:ktor-client-mock:$ktorVersion")

                // Koin
                implementation("io.insert-koin:koin-core:$koinVersion")
                implementation("io.insert-koin:koin-test:$koinVersion")

                // SqlDelight
                implementation("app.cash.sqldelight:sqlite-driver:$sqlDelightVersion")
            }
        }
        val androidMain by getting {
            dependencies {
                api("androidx.activity:activity-compose:1.7.2")
                api("androidx.appcompat:appcompat:1.6.1")
                api("androidx.core:core-ktx:1.10.1")

                // Ktor
                implementation("io.ktor:ktor-client-android:${ktorVersion}")

                // SqlDelight
                implementation("app.cash.sqldelight:android-driver:${sqlDelightVersion}")

                // Koin
                implementation("io.insert-koin:koin-android:$koinVersion")
            }
        }
        val desktopMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation(compose.desktop.common)
                implementation("org.apache.httpcomponents:httpclient:4.5.14")
                implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:$coroutinesVersion")

                // Ktor
                implementation("io.ktor:ktor-client-java:${ktorVersion}")

                // SqlDelight
                implementation("app.cash.sqldelight:sqlite-driver:${sqlDelightVersion}")
            }
        }
    }
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "com.weatherapp"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        minSdk = (findProperty("android.minSdk") as String).toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin {
        jvmToolchain(11)
    }
}

sqldelight {
    databases {
        create("WeatherDatabase") {
            packageName.set("com.weatherapp.core.database")
        }
    }
}