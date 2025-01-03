import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeFeatureFlag
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JsSourceMapNamesPolicy
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.roborazzi)
}

composeCompiler {
    featureFlags = setOf(
        ComposeFeatureFlag.OptimizeNonSkippingGroups,
        ComposeFeatureFlag.PausableComposition
    )
}

kotlin {
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        compilerOptions {
            sourceMapNamesPolicy.assign(JsSourceMapNamesPolicy.SOURCE_MAP_NAMES_POLICY_SIMPLE_NAMES)
        }
        moduleName = "nineMensMorrisApp"
        browser {
            testTask {
                useKarma {
                    useDebuggableChrome()
                }
            }
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                outputFileName = "composeApp.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(projectDirPath)
                    }
                }
            }
        }
        binaries.executable()
    }

    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        instrumentedTestVariant.sourceSetTree.set(KotlinSourceSetTree.test)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    jvm("desktop")

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        val desktopMain by getting
        iosMain.dependencies {
            implementation(libs.ktor.client.cio)
        }
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.cio)
            implementation(compose.uiTooling)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.decompose)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.decompose.jetbrains)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.multiplatform.settings)
            implementation(libs.multiplatform.settings.no.arg)
            implementation(libs.ninemensmorris)
            implementation(libs.filekit.compose)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.ktor.client.cio)
            implementation(compose.uiTooling)
        }
        wasmJsMain.dependencies {
            implementation(libs.ktor.client.js)
        }
    }
}

@OptIn(ExperimentalEncodingApi::class)
android {
    namespace = "com.kroune.nine_mens_morris_kmp_app"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "com.kroune.nine_mens_morris"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        dependencies {
            androidTestImplementation(libs.androidx.ui.test.junit4.android)
            debugImplementation(libs.androidx.ui.test.manifest)
        }
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    signingConfigs {
        create("release") {
            keyAlias = "upload"
            if (System.getenv("KEYSTORE") != null && System.getenv("KEYSTORE_PASSWORD") != null) {
                val file = File.createTempFile("keyStore", ".jks")
                file.writeBytes(Base64.decode(System.getenv("KEYSTORE")!!.toByteArray()))

                storeFile = file
                storePassword = System.getenv("KEYSTORE_PASSWORD")!!
                keyPassword = System.getenv("KEYSTORE_PASSWORD")!!
            } else {
                storeFile = file("/home/olowo/keystore.jks")
                storePassword = "53krita"
                keyPassword = "53krita"
            }
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
    dependencies {
        debugImplementation(compose.uiTooling)
    }
}

compose.desktop {
    application {
        buildTypes.release.proguard {
            configurationFiles.setFrom("proguard-rules.pro")
            version.set("7.6.1")
        }
        mainClass = "com.kroune.nine_mens_morris_kmp_app.MainKt"

        nativeDistributions {
            macOS {
                iconFile = project.file("icons/icon.icns")
            }
            linux {
                modules("jdk.security.auth")
                iconFile = project.file("icons/icon.png")
            }
            windows {
                iconFile = project.file("icons/icon.ico")
            }
            targetFormats(
                // macos
                TargetFormat.Dmg,
                // linux
                TargetFormat.Rpm, TargetFormat.Deb,
                // windows
                TargetFormat.Msi, TargetFormat.Exe
            )
            packageName = "nineMensMorris"
            packageVersion = "1.0.1"
            description = "Implementation of a table game called <Nine mens morris>"
            copyright = "© 2024 Kroune. All rights reserved."
            licenseFile = project.file("LICENSE")
        }
    }
}
