import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.googleServices)

    alias(libs.plugins.room)
    alias(libs.plugins.ksp)

    alias(libs.plugins.kotlin.serialization)

    alias(libs.plugins.gmazzo.buildconfig)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class) compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosX64(), iosArm64(), iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            export(project(":kmpnotifier"))
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.startup.runtime)

            implementation(libs.compose.ui)
            implementation(libs.androidx.activity.compose)
            implementation(libs.koin.android)
            implementation(libs.ktor.client.okhttp)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(compose.components.resources)
            api(project(":kmpnotifier"))
            implementation(project(":kmpauth-google"))
            implementation(project(":kmpauth-firebase"))
            implementation(project(":kmpauth-uihelper"))
            implementation(project(":kmpnotifier"))
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)

            implementation(libs.compose.navigation)
            implementation(libs.landscapist.coil3)
            implementation(libs.room.runtime)
            implementation(libs.sqlite.bundled)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)

            implementation(libs.bundles.ktor)

            // qrcode
            implementation(libs.qr.kit)
        }
    }
}

var keystoreProperties = Properties()
println("BEGIN")

if (gradle.startParameter.taskNames.any { it.contains("Release") }){
    println("RELEASE")
    val keystorePropertiesFile = rootProject.file("../PuntoEttoreExtraFilesProd/keystore-release.properties")
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
    copy {
        from(project.rootDir.absolutePath + "/../PuntoEttoreExtraFilesProd/composeApp/google-services.json")
        into(project.rootDir.absolutePath + "/composeApp")
        // rename("google-services.json", "google-services.json")
        println("Copy google-services.json to ${project.rootDir.absolutePath}/composeApp")
    }
    copy {
        from(project.rootDir.absolutePath + "/../PuntoEttoreExtraFilesProd/iosApp/iosApp/GoogleService-Info.plist")
        into(project.rootDir.absolutePath + "/iosApp/iosApp")
        println("Copy GoogleService-Info.plist to ${project.rootDir.absolutePath}/iosApp/iosApp")
    }
    copy {
        from(project.rootDir.absolutePath + "/../PuntoEttoreExtraFilesProd/iosApp/iosApp/Info.plist")
        into(project.rootDir.absolutePath + "/iosApp/iosApp")
        println("Copy Info.plist to ${project.rootDir.absolutePath}/iosApp/iosApp")
    }
} else {
    println("DEBUG")
    val keystorePropertiesFile = rootProject.file("../PuntoEttoreExtraFilesTest/keystore-debug.properties")
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
    copy {
        from(project.rootDir.absolutePath + "/../PuntoEttoreExtraFilesTest/composeApp/google-services.json")
        into(project.rootDir.absolutePath + "/composeApp")
        // rename("google-services.json", "google-services.json")
        println("Copy Debug to ComposeApp " + project.rootDir.absolutePath)
    }
    copy {
        from(project.rootDir.absolutePath + "/../PuntoEttoreExtraFilesTest/iosApp/iosApp/GoogleService-Info.plist")
        into(project.rootDir.absolutePath + "/iosApp/iosApp")
        println("Copy GoogleService-Info.plist to ${project.rootDir.absolutePath}/iosApp/iosApp")
    }
    copy {
        from(project.rootDir.absolutePath + "/../PuntoEttoreExtraFilesTest/iosApp/iosApp/Info.plist")
        into(project.rootDir.absolutePath + "/iosApp/iosApp")
        println("Copy Info.plist to ${project.rootDir.absolutePath}/iosApp/iosApp")
    }
}

android {

    signingConfigs {
        getByName("debug") {
            println(keystoreProperties["storeFile"] as String)
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
        }
        create("release") {
            println(keystoreProperties["storeFile"] as String)
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
        }
    }

    namespace = "it.puntoettore.fidelity"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources") //, "src/commonMain/composeResources"

    defaultConfig {
        applicationId = "it.puntoettore.fidelity"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 2
        versionName = "1.1"
        signingConfig = signingConfigs.getByName("debug")
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
//    flavorDimensions += listOf("paid_status", "theme")
//    productFlavors {
//        create("free") {
//            dimension = "paid_status"
//            applicationId = "it.puntoettore.fidelity"
//            minSdk = libs.versions.android.minSdk.get().toInt()
//            targetSdk = libs.versions.android.targetSdk.get().toInt()
//            versionCode = 1
//            versionName = "1.0"
//            signingConfig = signingConfigs.getByName("debug")
//        }
//        create("paid") {
//            dimension = "paid_status"
//            applicationId = "it.puntoettore.fidelity"
//            minSdk = libs.versions.android.minSdk.get().toInt()
//            targetSdk = libs.versions.android.targetSdk.get().toInt()
//            versionCode = 1
//            versionName = "1.0"
//            signingConfig = signingConfigs.getByName("debug")
//        }
//
//        create("green") {
//            dimension = "theme"
//            applicationId = "it.puntoettore.fidelity"
//            minSdk = libs.versions.android.minSdk.get().toInt()
//            targetSdk = libs.versions.android.targetSdk.get().toInt()
//            versionCode = 1
//            versionName = "1.0"
//            signingConfig = signingConfigs.getByName("debug")
//        }
//        create("red") {
//            dimension = "theme"
//            applicationId = "it.puntoettore.fidelity"
//            minSdk = libs.versions.android.minSdk.get().toInt()
//            targetSdk = libs.versions.android.targetSdk.get().toInt()
//            versionCode = 1
//            versionName = "1.0"
//            signingConfig = signingConfigs.getByName("debug")
//        }
//    }

    buildTypes {
        getByName("debug") {
            isDebuggable = true
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
            applicationIdSuffix = ".test"
            resValue("string", "app_name", "** Punto Ettore Fidelity Test **")
        }
        getByName("release") {
            isMinifyEnabled = false
            isDebuggable = false
            applicationIdSuffix = ".prod"
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    dependencies {
        debugImplementation(libs.compose.ui.tooling)
        implementation(project.dependencies.platform("com.google.firebase:firebase-bom:33.3.0"))
    }

    // Release
    tasks.register("getAppName") {
        // Set the task"s actions
        doLast {
            val versionName = defaultConfig.versionName
            val versionCode = defaultConfig.versionCode
            val appName = "APP-v${versionName}-$versionCode"
            // Print the app name
            println(appName)
        }
    }
}

buildConfig {
    // className("BuildConfig")   // forces the class name. Defaults to 'BuildConfig'
    // packageName("${project.group}")  // forces the package. Defaults to '${project.group}'
    // root
    // buildConfigField("APP_NAME", project.name)

    useJavaOutput()
    useKotlinOutput()                               // forces the outputType to 'kotlin', generating an `object`
    useKotlinOutput {
        topLevelConstants = true
    }    // forces the outputType to 'kotlin', generating top-level declarations
    useKotlinOutput { internalVisibility = true }   // adds `internal` modifier to all declarations

    forClass(packageName = "${android.namespace}.custom", className = "BuildConfig") {
        // forClass(className = "BuildConfig") {
        // buildConfigField("APP_NAME", project.name)
        buildConfigField("APP_VERSION", provider { "\"${project.version}\"" })
        buildConfigField("APP_SECRET", "Z3JhZGxlLWphdmEtYnVpbGRjb25maWctcGx1Z2lu")
        buildConfigField<String>("OPTIONAL", null)
        buildConfigField("BUILD_TIME", System.currentTimeMillis())
        buildConfigField("FEATURE_ENABLED", true)
        buildConfigField("MAGIC_NUMBERS", intArrayOf(1, 2, 3, 4))
        buildConfigField("STRING_LIST", arrayOf("a", "b", "c"))
        buildConfigField("MAP", mapOf("a" to 1, "b" to 2))
//        buildConfigField("FILE", File("aFile"))
//        buildConfigField("URI", uri("https://example.io"))
        buildConfigField("END_POINT", keystoreProperties["endpoint"] as String)

        buildConfigField("SERVER_ID", keystoreProperties["serverId"] as String)

        if (gradle.startParameter.taskNames.any { it.contains("Release") }) {
            // Definisci le proprietà per la variante release
            println("SEI IN RELEASE")
            buildConfigField("IS_DEBUG", false)
        } else {
            // Definisci le proprietà per la variante debug
            println("SEI IN DEBUG")
            buildConfigField("IS_DEBUG", true)
        }
    }

    println("SEI IN ROOT")

//    this.sourceSets.getByName("iosMain") {
//
//        className("BuildConfig")   // forces the class name. Defaults to 'BuildConfig'
//        packageName("${project.group}")  // forces the package. Defaults to '${project.group}'
//        // root
//        // buildConfigField("APP_NAME", project.name)
//        forClass(packageName = "${android.namespace}", className = "BuildConfig") {
//            // buildConfigField("APP_NAME", project.name)
//            buildConfigField("APP_VERSION", provider { "\"${project.version}\"" })
//            buildConfigField("APP_SECRET", "Z3JhZGxlLWphdmEtYnVpbGRjb25maWctcGx1Z2lu")
//            buildConfigField<String>("OPTIONAL", null)
//            buildConfigField("BUILD_TIME", System.currentTimeMillis())
//            buildConfigField("FEATURE_ENABLED", true)
//            buildConfigField("MAGIC_NUMBERS", intArrayOf(1, 2, 3, 4))
//            buildConfigField("STRING_LIST", arrayOf("a", "b", "c"))
//            buildConfigField("MAP", mapOf("a" to 1, "b" to 2))
//            buildConfigField("FILE", File("aFile"))
//            buildConfigField("URI", uri("https://example.io"))
//            buildConfigField("END_POINT", keystoreProperties["endpoint"] as String)
////            buildConfigField(
////                "com.github.gmazzo.buildconfig.demos.kts.SomeData",
////                "DATA",
////                "SomeData(\"a\", 1)"
////            )
//        }
//    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    debugImplementation(compose.uiTooling)
    add("kspAndroid", libs.room.compiler)
    add("kspIosX64", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
}

compose.resources {
    publicResClass = true
    packageOfResClass = "it.puntoettore.fidelity"
    generateResClass = auto
}
