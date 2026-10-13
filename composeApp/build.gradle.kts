import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    android {
        namespace = "dev.watchstore.lib"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    // -------------------------------------------------------------------------
    // iOS targets.
    //
    // Deliberately NOT enabled yet. Kotlin/Native can compile Apple klibs from
    // Windows, but it pulls a ~1 GB macOS toolchain download and needs a Mac for
    // final binaries, CocoaPods and simulator runs.
    //
    // To enable later on a machine that is ready for it, uncomment below and add
    // the `iosApp/` Xcode host project. Nothing in commonMain changes.
    //
    //   iosArm64()
    //   iosSimulatorArm64()
    // -------------------------------------------------------------------------

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.ui)
            implementation(libs.compose.ui.backhandler)
            implementation(libs.compose.material3)
            implementation(libs.compose.icons.core)
            implementation(libs.compose.resources)

            implementation(libs.kotlinx.serialization.json)
            implementation(libs.navigation3.ui)
            implementation(libs.lifecycle.viewmodel)
            implementation(libs.lifecycle.viewmodel.compose)
            implementation(libs.lifecycle.runtime.compose)
        }
    }
}

compose.resources {
    // Generated accessors default to `internal`, and their package is derived
    // from the Gradle project name. Both are wrong for us: the design system
    // lives in a different package, and renaming the project would silently
    // break every resource import.
    publicResClass = true
    packageOfResClass = "dev.watchstore.resources"
}