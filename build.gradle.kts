import org.gradle.api.tasks.Sync

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeHotReload) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.androidKotlinMultiplatformLibrary) apply false
    alias(libs.plugins.androidLint) apply false
}

// Compose resources expose Skiko runtime modules from core:common. Kotlin/JS
// test packages do not currently stage those modules for dependent projects,
// even though generated JS imports them relatively. Stage the generated runtime
// before each browser test so webpack tests the real browser dependency graph.
subprojects {
    pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
        val commonProject = rootProject.project(":core:common")
        val packageName = "rasteh-${path.removePrefix(":").replace(':', '-')}-test"
        val stageComposeRuntimeForJsBrowserTest = tasks.register<Sync>("stageComposeRuntimeForJsBrowserTest") {
            dependsOn(commonProject.tasks.named("jsProcessResources"))
            from(commonProject.layout.buildDirectory.dir("processedResources/js/main")) {
                include("skiko.mjs", "skikod8.mjs", "js-reexport-symbols.mjs")
            }
            into(rootProject.layout.buildDirectory.dir("js/packages/$packageName/kotlin"))
        }

        tasks.matching { it.name == "jsBrowserTest" }.configureEach {
            dependsOn(stageComposeRuntimeForJsBrowserTest)
        }
    }
}
