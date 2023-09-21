import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

kotlin {
    jvm()
    val koinVersion = "3.2.0"
    val kotlinDecomposeVersion = "1.0.0-compose-experimental"
    val mviKotlinVersion = "3.1.0"

    sourceSets {
        val jvmMain by getting  {
            dependencies {
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
        }
    }
}
