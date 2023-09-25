import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

kotlin {
    jvm {
        jvmToolchain(11)
        withJava()
    }
    val koinVersion = "3.2.0"
    val kotlinDecomposeVersion = "1.0.0-compose-experimental"
    val mviKotlinVersion = "3.1.0"
    val dateTimeVersion = "0.4.1"

    sourceSets {
        val jvmMain by getting  {
            dependencies {
                implementation(project(":shared"))

                // Koin
                implementation("io.insert-koin:koin-core:$koinVersion")
                implementation("io.insert-koin:koin-test:$koinVersion")
                //Decompose
                implementation("com.arkivanov.decompose:decompose:$kotlinDecomposeVersion")
                implementation("com.arkivanov.decompose:extensions-compose-jetbrains:$kotlinDecomposeVersion")
                implementation(compose.desktop.currentOs)
                implementation(project(":shared"))
                // MVIKotlin
                implementation("com.arkivanov.mvikotlin:mvikotlin:$mviKotlinVersion")
                implementation("com.arkivanov.mvikotlin:mvikotlin-main:$mviKotlinVersion")
                implementation("com.arkivanov.mvikotlin:mvikotlin-extensions-coroutines:$mviKotlinVersion")

                // Kotlin Date Library
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:$dateTimeVersion")

                implementation("org.jetbrains.compose.material:material-icons-extended:1.5.0")
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "KotlinMultiplatformComposeDesktopApplication"
            packageVersion = "1.0.0"
            windows {
                // a version for all Windows distributables
                packageVersion = "1.0.0"
                // a version only for the msi package
                msiPackageVersion = "1.0.0"
                // a version only for the exe package
                exePackageVersion = "1.0.0"
            }
        }
    }
}
