import com.android.build.gradle.LibraryExtension
import java.io.ByteArrayOutputStream

plugins {
    // Use the standard Android Library plugin directly
    id("com.android.library") version "8.5.2" apply false
}

fun String.execute(currentWorkingDir: File = File("./")): String {
    val byteOut = ByteArrayOutputStream()
    project.exec {
        workingDir = currentWorkingDir
        commandLine = this@execute.split("\\s+".toRegex())
        standardOutput = byteOut
        errorOutput = byteOut
    }
    return byteOut.toString().trim()
}

// --- Git info ---
val gitCommitCount = "git rev-list HEAD --count".execute().toInt()
val gitCommitHash = "git rev-parse --verify --short HEAD".execute()

// --- Project metadata ---
extra["moduleId"] = "rezygisk"
extra["moduleName"] = "ReZygisk"
extra["verName"] = "v1.0.0"
extra["verCode"] = gitCommitCount
extra["commitHash"] = gitCommitHash
extra["minAPatchVersion"] = 10655
extra["minKsuVersion"] = 10940
extra["minKsudVersion"] = 11425
extra["minMagiskVersion"] = 26402
extra["androidMinSdkVersion"] = 26
extra["androidTargetSdkVersion"] = 34
extra["androidCompileSdkVersion"] = 34
extra["androidBuildToolsVersion"] = "34.0.0"
extra["androidCompileNdkVersion"] = "27.2.12479018"
extra["androidSourceCompatibility"] = JavaVersion.VERSION_11
extra["androidTargetCompatibility"] = JavaVersion.VERSION_11

// --- Clean task ---
tasks.register<Delete>("clean") {
    delete(layout.buildDirectory)
}

// --- Configure all library modules ---
fun Project.configureBaseExtension() {
    extensions.findByType(LibraryExtension::class)?.apply {
        namespace = "com.performanc.org.rezygisk"
        compileSdk = extra["androidCompileSdkVersion"] as Int
        ndkVersion = extra["androidCompileNdkVersion"] as String
        buildToolsVersion = extra["androidBuildToolsVersion"] as String

        defaultConfig {
            minSdk = extra["androidMinSdkVersion"] as Int
        }

        lint {
            abortOnError = true
        }

        compileOptions {
            sourceCompatibility = extra["androidSourceCompatibility"] as JavaVersion
            targetCompatibility = extra["androidTargetCompatibility"] as JavaVersion
        }
    }
}

subprojects {
    plugins.withId("com.android.library") {
        configureBaseExtension()
    }
}
