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

            // date
            implementation(libs.kotlinx.datetime)

            // new scaffolding for material Icons
            implementation(compose.materialIconsExtended)
        }
    }
}

android {

    signingConfigs {
        create("production") {
            val keystoreProperties = Properties()
            println("BEGIN")
            println("import signin release keys")
            val keystorePropertiesFile =
                rootProject.file("../PuntoEttoreExtraFiles/PuntoEttoreExtraFilesProd/keystore-release.properties")
            keystoreProperties.load(FileInputStream(keystorePropertiesFile))

            println(keystoreProperties["storeFile"] as String)
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String

            copy {
                from(project.rootDir.absolutePath + "/../PuntoEttoreExtraFiles/PuntoEttoreExtraFilesProd/composeApp/src/production/release/google-services.json")
                into(project.rootDir.absolutePath + "/composeApp/src/production/release")
                println("Copy google-services.json to ComposeApp to ${project.rootDir.absolutePath}/composeApp/src/production/release")
            }
            copy {
                from(project.rootDir.absolutePath + "/../PuntoEttoreExtraFiles/PuntoEttoreExtraFilesProd/composeApp/src/production/debug/google-services.json")
                into(project.rootDir.absolutePath + "/composeApp/src/production/debug")
                println("Copy google-services.json to ComposeApp to ${project.rootDir.absolutePath}/composeApp/src/production/debug")
            }
            copy {
                from(project.rootDir.absolutePath + "/../PuntoEttoreExtraFiles/PuntoEttoreExtraFilesProd/iosApp/iosApp/GoogleService-Info.plist")
                into(project.rootDir.absolutePath + "/iosApp/iosApp")
                println("Copy GoogleService-Info.plist to ${project.rootDir.absolutePath}/iosApp/iosApp")
            }
            copy {
                from(project.rootDir.absolutePath + "/../PuntoEttoreExtraFiles/PuntoEttoreExtraFilesProd/iosApp/iosApp/Info.plist")
                into(project.rootDir.absolutePath + "/iosApp/iosApp")
                println("Copy Info.plist to ${project.rootDir.absolutePath}/iosApp/iosApp")
            }
            copy {
                from(project.rootDir.absolutePath + "/../PuntoEttoreExtraFiles/PuntoEttoreExtraFilesProd/iosApp/iosApp/ExportOptions.plist")
                into(project.rootDir.absolutePath + "/iosApp/iosApp")
                println("Copy Info.plist to ${project.rootDir.absolutePath}/iosApp/iosApp")
            }
        }
        create("internal") {
            val keystoreProperties = Properties()
            println("BEGIN")
            println("import signin debug keys")
            val keystorePropertiesFile =
                rootProject.file("../PuntoEttoreExtraFiles/PuntoEttoreExtraFilesTest/keystore-debug.properties")
            keystoreProperties.load(FileInputStream(keystorePropertiesFile))

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

    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    // 1. Definisci la dimensione
    flavorDimensions += "version"

    // Aggiungi questo blocco
    productFlavors {
        create("internal") {
            dimension = "version"
            // Configurazione specifica per la versione "internal"
            applicationIdSuffix = ".test"
            versionNameSuffix = "-TEST"
            signingConfig = signingConfigs.getByName("internal")
            resValue("string", "app_name", "** Punto Ettore Fidelity Test **")
        }
        create("production") {
            dimension = "version"
            // Configurazione specifica per la versione "production"
            applicationIdSuffix = ".prod"
            versionNameSuffix = "-PROD"
            signingConfig = signingConfigs.getByName("production")
            resValue("string", "app_name", "Punto Ettore Fidelity")
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
            isMinifyEnabled = false
        }
        release {
            isDebuggable = true
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    defaultConfig {

        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 13
        versionName = "13"
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
    useJavaOutput()
    useKotlinOutput()                               // forces the outputType to 'kotlin', generating an `object`
    useKotlinOutput {
        topLevelConstants = true
    }    // forces the outputType to 'kotlin', generating top-level declarations
    useKotlinOutput { internalVisibility = true }   // adds `internal` modifier to all declarations

    // Fai in modo che il task di Compose dipenda dal tuo
    tasks.named("embedAndSignAppleFrameworkForXcode") {
        println("NOME BUILD da IOS-> " + gradle.startParameter.taskNames)
        if (gradle.startParameter.taskNames.any {
                it.contains("Release")
            }) {
            // PRODUZIONE
            // TODO : verificare che i certificati caricati e .list siano corretti
            //dependsOn(tasks.named("ProductionRelease"))
        } else if (gradle.startParameter.taskNames.any {
                it.contains("Debug")
            }) {
            // DEBUG
            // TODO : verificare che i certificati caricati e .list siano corretti
            //dependsOn(tasks.named("InternalRelease"))
        }

    }

    // creazioni variabili da usare a codice per tipo BUILD
    forClass(packageName = "${android.namespace}.custom", className = "BuildConfig") {
        println("NOME BUILD -> " + gradle.startParameter.taskNames)
        if (gradle.startParameter.taskNames.any {
                it.contains("ProductionRelease")
            }) {
            // Definisci le proprietà per la variante release
            println("ProductionRelease")
            buildConfigField("IS_DEBUG", false)
            val keystoreProperties = Properties()
            val keystorePropertiesFile =
                rootProject.file("../PuntoEttoreExtraFiles/PuntoEttoreExtraFilesProd/keystore-release.properties")
            keystoreProperties.load(FileInputStream(keystorePropertiesFile))
            buildConfigField("END_POINT", keystoreProperties["endpoint"] as String)
            buildConfigField("SERVER_ID", keystoreProperties["serverId"] as String)
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

            copy {
                from(project.rootDir.absolutePath + "/../PuntoEttoreExtraFiles/PuntoEttoreExtraFilesProd/composeApp/src/production/release/google-services.json")
                into(project.rootDir.absolutePath + "/composeApp/src/production/release")
                println("Copy google-services.json to ComposeApp to ${project.rootDir.absolutePath}/composeApp/src/production/release")
            }
            copy {
                from(project.rootDir.absolutePath + "/../PuntoEttoreExtraFiles/PuntoEttoreExtraFilesProd/iosApp/iosApp/GoogleService-Info.plist")
                into(project.rootDir.absolutePath + "/iosApp/iosApp")
                println("Copy GoogleService-Info.plist to ${project.rootDir.absolutePath}/iosApp/iosApp")
            }
            copy {
                from(project.rootDir.absolutePath + "/../PuntoEttoreExtraFiles/PuntoEttoreExtraFilesProd/iosApp/iosApp/Info.plist")
                into(project.rootDir.absolutePath + "/iosApp/iosApp")
                println("Copy Info.plist to ${project.rootDir.absolutePath}/iosApp/iosApp")
            }
            copy {
                from(project.rootDir.absolutePath + "/../PuntoEttoreExtraFiles/PuntoEttoreExtraFilesProd/iosApp/iosApp/ExportOptions.plist")
                into(project.rootDir.absolutePath + "/iosApp/iosApp")
                println("Copy Info.plist to ${project.rootDir.absolutePath}/iosApp/iosApp")
            }

        } else if (gradle.startParameter.taskNames.any {
                it.contains("ProductionDebug")
            }) {
            // Definisci le proprietà per la variante release
            println("ProductionDebug")
            buildConfigField("IS_DEBUG", false)
            val keystoreProperties = Properties()
            val keystorePropertiesFile =
                rootProject.file("../PuntoEttoreExtraFiles/PuntoEttoreExtraFilesProd/keystore-release.properties")
            keystoreProperties.load(FileInputStream(keystorePropertiesFile))
            buildConfigField("END_POINT", keystoreProperties["endpoint"] as String)
            buildConfigField("SERVER_ID", keystoreProperties["serverId"] as String)
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

            copy {
                from(project.rootDir.absolutePath + "/../PuntoEttoreExtraFiles/PuntoEttoreExtraFilesProd/composeApp/src/production/debug/google-services.json")
                into(project.rootDir.absolutePath + "/composeApp/src/production/debug")
                println("Copy google-services.json to ComposeApp to ${project.rootDir.absolutePath}/composeApp/src/production/debug")
            }
            copy {
                from(project.rootDir.absolutePath + "/../PuntoEttoreExtraFiles/PuntoEttoreExtraFilesProd/iosApp/iosApp/GoogleService-Info.plist")
                into(project.rootDir.absolutePath + "/iosApp/iosApp")
                println("Copy GoogleService-Info.plist to ${project.rootDir.absolutePath}/iosApp/iosApp")
            }
            copy {
                from(project.rootDir.absolutePath + "/../PuntoEttoreExtraFiles/PuntoEttoreExtraFilesProd/iosApp/iosApp/Info.plist")
                into(project.rootDir.absolutePath + "/iosApp/iosApp")
                println("Copy Info.plist to ${project.rootDir.absolutePath}/iosApp/iosApp")
            }
            copy {
                from(project.rootDir.absolutePath + "/../PuntoEttoreExtraFiles/PuntoEttoreExtraFilesProd/iosApp/iosApp/ExportOptions.plist")
                into(project.rootDir.absolutePath + "/iosApp/iosApp")
                println("Copy Info.plist to ${project.rootDir.absolutePath}/iosApp/iosApp")
            }

        } else
            if (gradle.startParameter.taskNames.any {
                    it.contains("InternalRelease")
                }) {

                // Definisci le proprietà per la variante release
                println("InternalRelease")
                buildConfigField("IS_DEBUG", false)
                val keystoreProperties = Properties()
                val keystorePropertiesFile =
                    rootProject.file("../PuntoEttoreExtraFiles/PuntoEttoreExtraFilesTest/keystore-debug.properties")
                keystoreProperties.load(FileInputStream(keystorePropertiesFile))
                buildConfigField("END_POINT", keystoreProperties["endpoint"] as String)
                buildConfigField("SERVER_ID", keystoreProperties["serverId"] as String)
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

                copy {
                    from(project.rootDir.absolutePath + "/../PuntoEttoreExtraFiles/PuntoEttoreExtraFilesTest/composeApp/src/internal/release/google-services.json")
                    into(project.rootDir.absolutePath + "/composeApp/src/internal/release")
                    println("Copy google-services.json to ComposeApp to ${project.rootDir.absolutePath}/composeApp/src/internal/release")
                }
                copy {
                    from(project.rootDir.absolutePath + "/../PuntoEttoreExtraFiles/PuntoEttoreExtraFilesTest/iosApp/iosApp/GoogleService-Info.plist")
                    into(project.rootDir.absolutePath + "/iosApp/iosApp")
                    println("Copy GoogleService-Info.plist to ${project.rootDir.absolutePath}/iosApp/iosApp")
                }
                copy {
                    from(project.rootDir.absolutePath + "/../PuntoEttoreExtraFiles/PuntoEttoreExtraFilesTest/iosApp/iosApp/Info.plist")
                    into(project.rootDir.absolutePath + "/iosApp/iosApp")
                    println("Copy Info.plist to ${project.rootDir.absolutePath}/iosApp/iosApp")
                }
                copy {
                    from(project.rootDir.absolutePath + "/../PuntoEttoreExtraFiles/PuntoEttoreExtraFilesTest/iosApp/iosApp/ExportOptions.plist")
                    into(project.rootDir.absolutePath + "/iosApp/iosApp")
                    println("Copy Info.plist to ${project.rootDir.absolutePath}/iosApp/iosApp")
                }

            } else if (gradle.startParameter.taskNames.any { it.contains("InternalDebug") }) {
                println("InternalDebug")
                buildConfigField("IS_DEBUG", true)
                val keystoreProperties = Properties()
                val keystorePropertiesFile =
                    rootProject.file("../PuntoEttoreExtraFiles/PuntoEttoreExtraFilesTest/keystore-debug.properties")
                keystoreProperties.load(FileInputStream(keystorePropertiesFile))
                buildConfigField("END_POINT", keystoreProperties["endpoint"] as String)
                buildConfigField("SERVER_ID", keystoreProperties["serverId"] as String)

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
                // buildConfigField("FILE", File("aFile"))
                // buildConfigField("URI", uri("https://example.io"))

                copy {
                    from(project.rootDir.absolutePath + "/../PuntoEttoreExtraFiles/PuntoEttoreExtraFilesTest/composeApp/src/internal/debug/google-services.json")
                    into(project.rootDir.absolutePath + "/composeApp/src/internal/debug")
                    println("Copy google-services.json to ComposeApp to ${project.rootDir.absolutePath}/composeApp/src/internal/debug")
                }
                copy {
                    from(project.rootDir.absolutePath + "/../PuntoEttoreExtraFiles/PuntoEttoreExtraFilesTest/iosApp/iosApp/GoogleService-Info.plist")
                    into(project.rootDir.absolutePath + "/iosApp/iosApp")
                    println("Copy GoogleService-Info.plist to ${project.rootDir.absolutePath}/iosApp/iosApp")
                }
                copy {
                    from(project.rootDir.absolutePath + "/../PuntoEttoreExtraFiles/PuntoEttoreExtraFilesTest/iosApp/iosApp/Info.plist")
                    into(project.rootDir.absolutePath + "/iosApp/iosApp")
                    println("Copy Info.plist to ${project.rootDir.absolutePath}/iosApp/iosApp")
                }
                copy {
                    from(project.rootDir.absolutePath + "/../PuntoEttoreExtraFiles/PuntoEttoreExtraFilesTest/iosApp/iosApp/ExportOptions.plist")
                    into(project.rootDir.absolutePath + "/iosApp/iosApp")
                    println("Copy Info.plist to ${project.rootDir.absolutePath}/iosApp/iosApp")
                }
            }
    }

    println("SEI IN ROOT")
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
