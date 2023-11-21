// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.4" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("com.diffplug.spotless") version "6.19.0"
}

subprojects {
    apply(plugin = "com.diffplug.spotless")
    configure<com.diffplug.gradle.spotless.SpotlessExtension> {

        java {
            target("app/src/**/*.java")
            googleJavaFormat("1.17.0").aosp()
            importOrder("android", "androidx", "com", "java", "phone", "")

            removeUnusedImports()
            licenseHeaderFile(rootProject.file("spotless-header"))
        }

        kotlin {
            target("app/src/**/*.kt")
            ktlint("0.49.1")
            trimTrailingWhitespace()

            licenseHeaderFile(rootProject.file("spotless-header"))
        }

        kotlinGradle {
            target("*.gradle.kts")
            ktlint()
        }
    }
}