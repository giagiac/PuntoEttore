plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinNativeCocoaPods) apply false


    alias(libs.plugins.googleServices) apply false
    alias(libs.plugins.kotlinx.binary.validator)

    alias(libs.plugins.kotlin.serialization) apply false

    alias(libs.plugins.gmazzo.buildconfig) apply false

}

allprojects {
    // group = "io.github.mirzemehdi"
    version = project.properties["kmpAuthVersion"] as String
}
