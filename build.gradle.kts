// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.google.ksp) apply false
    alias(libs.plugins.google.hilt) apply false
    alias(libs.plugins.compose.compiler) apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

true // Needed to make the Suppress annotation work for the plugins block