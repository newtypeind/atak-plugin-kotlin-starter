import java.util.Properties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

////////////////////////////////////////////////////////////////////////////////
// PLUGIN_VERSION  describes this plugin.
// ATAK_VERSION    is the ATAK version this plugin targets.
//
// The ATAK SDK location is NOT hardcoded or found via a relative path. Set
// `sdk.path` (the ATAK SDK directory that contains main.jar and
// atak-gradle-takdev.jar) in local.properties or gradle.properties. takdev reads
// `sdk.path` to locate main.jar; the buildscript below resolves the takdev jar
// from it. Optional overrides: `takdev.plugin` (explicit jar path) or
// `takrepo.url` (build against a Maven repo instead of the offline SDK).
////////////////////////////////////////////////////////////////////////////////

buildscript {
    // Read a value from -P / gradle.properties first, then local.properties.
    val configProp: (String) -> String? = { key ->
        (findProperty(key) as String?) ?: run {
            val f = rootProject.file("local.properties")
            if (f.isFile) {
                java.util.Properties().apply { f.inputStream().use { load(it) } }.getProperty(key)
            } else {
                null
            }
        }
    }

    val takrepoUrl = configProp("takrepo.url")
    val sdkPath = configProp("sdk.path")
    val takdevJar = configProp("takdev.plugin") ?: sdkPath?.let { "$it/atak-gradle-takdev.jar" }

    repositories {
        google()
        mavenCentral()
        mavenLocal()
        maven { url = uri("https://jitpack.io") }
        if (takrepoUrl != null) {
            maven {
                url = uri(takrepoUrl)
                credentials {
                    username = configProp("takrepo.user") ?: "invalid"
                    password = configProp("takrepo.password") ?: "invalid"
                }
            }
        }
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.13.0")
        if (takrepoUrl != null) {
            classpath("com.atakmap.gradle:atak-gradle-takdev:3.+")
        } else {
            requireNotNull(takdevJar) {
                "ATAK SDK not configured. Set 'sdk.path' (the ATAK SDK directory), or " +
                    "'takdev.plugin' (path to atak-gradle-takdev.jar), or 'takrepo.url', in " +
                    "local.properties or gradle.properties."
            }
            require(file(takdevJar).isFile) {
                "takdev jar not found at '$takdevJar'. Fix 'sdk.path' or 'takdev.plugin' in " +
                    "local.properties / gradle.properties."
            }
            classpath(files(takdevJar))
        }
    }
}

plugins {
    id("com.android.application")
}

// Reads -P / gradle.properties first, then local.properties (project scope).
fun configProp(key: String): String? =
    (findProperty(key) as String?) ?: run {
        val f = rootProject.file("local.properties")
        if (f.isFile) {
            Properties().apply { f.inputStream().use { load(it) } }.getProperty(key)
        } else {
            null
        }
    }

// Project ext the takdev plugin reads. The takdev plugin calls
// project.isDevKitEnabled(), so it must exist as a Groovy closure.
extra["PLUGIN_VERSION"] = "0.2"
// ATAK version this plugin declares as its `plugin-api` (the host matches it at
// load). Develop against the 5.7.0 SDK by default; override for a release build
// targeting another ATAK (e.g. `-Patak.version=5.6.0`, paired with that SDK's
// `sdk.path`). Set the key `atak.version` in gradle.properties for a durable
// override.
extra["ATAK_VERSION"] = configProp("atak.version") ?: "5.7.0"
extra["takrepoUrl"] = configProp("takrepo.url") ?: "https://localhost/"
extra["takrepoUser"] = configProp("takrepo.user") ?: "invalid"
extra["takrepoPassword"] = configProp("takrepo.password") ?: "invalid"
extra["takdevPlugin"] =
    configProp("takdev.plugin") ?: configProp("sdk.path")?.let { "$it/atak-gradle-takdev.jar" } ?: ""
val devKitEnabled = configProp("takrepo.url") != null
extra["isDevKitEnabled"] = object : groovy.lang.Closure<Boolean>(this, this) {
    fun doCall(): Boolean = devKitEnabled
}

// Offline builds resolve the ATAK SDK from the developer-provided `sdk.path`, not
// from this plugin's location. Require it and validate main.jar is there, so the
// build depends on the configured path instead of where the project happens to sit.
if (!devKitEnabled) {
    val sdkPath = configProp("sdk.path")
        ?: error(
            "Offline build requires 'sdk.path' (the ATAK SDK directory containing " +
                "main.jar and atak-gradle-takdev.jar) in local.properties or gradle.properties.",
        )
    require(file("$sdkPath/main.jar").isFile) {
        "ATAK SDK main.jar not found at '$sdkPath/main.jar'. Fix 'sdk.path' in " +
            "local.properties / gradle.properties."
    }
}

apply(plugin = "atak-takdev-plugin")
apply(plugin = "org.jetbrains.kotlin.android")
apply(plugin = "org.jetbrains.kotlin.plugin.compose")

allprojects {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        maven { url = uri("https://jitpack.io") }
    }
}

val pluginVersion = extra["PLUGIN_VERSION"] as String
val atakVersion = extra["ATAK_VERSION"] as String

// takdev injects getVersionName()/getVersionCode() as project ext closures. Call
// them if present, else fall back to git so the build never hard-depends on them.
fun takdevExt(key: String): Any? = (extra.properties[key] as? groovy.lang.Closure<*>)?.call()
fun gitOutput(vararg args: String): String? = runCatching {
    providers.exec { commandLine(*args) }.standardOutput.asText.get().trim().ifEmpty { null }
}.getOrNull()

val gitVersionName: String = (takdevExt("getVersionName") as? String)
    ?: gitOutput("git", "rev-parse", "--short", "HEAD") ?: "dev"
val pluginVersionCode: Int = (takdevExt("getVersionCode") as? Number)?.toInt()
    ?: gitOutput("git", "rev-list", "--count", "HEAD")?.toIntOrNull() ?: 1

// ISO tricode flavors. The starter ships only `civ` (ATAK CIV). Add more entries
// (e.g. "mil" to false) to target other ATAK editions; each gets its own
// `src/<flavor>` source set and `com.atakmap.app@<version>.<EDITION>` api tag.
val supportedFlavors = listOf(
    "civ" to true,
)

base {
    archivesName.set("ATAK-Plugin-${rootProject.name}-$pluginVersion-$gitVersionName-$atakVersion")
}

android {
    testOptions {
        unitTests.all { it.useJUnitPlatform() }
    }

    compileSdk = 36
    namespace = "com.atakmap.android.kotlinstarter"

    buildFeatures {
        buildConfig = true
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // Required: do not remove, otherwise the TAK inner signing check fails.
    bundle {
        storeArchive {
            enable = false
        }
    }

    // Debug/release both sign with the ATAK debug keystore that the takdev plugin
    // auto-generates into the build dir (alias/passwords are the SDK's standard
    // debug values). No manual keystore is needed for development builds. For a
    // real release, point these at your own keystore.
    signingConfigs {
        getByName("debug") {
            storeFile = layout.buildDirectory.file("android_keystore").get().asFile
            storePassword = "tnttnt"
            keyAlias = "wintec_mapping"
            keyPassword = "tnttnt"
        }
        create("release") {
            storeFile = layout.buildDirectory.file("android_keystore").get().asFile
            storePassword = "tnttnt"
            keyAlias = "wintec_mapping"
            keyPassword = "tnttnt"
        }
    }

    buildTypes {
        getByName("debug") {
            isDebuggable = true
            matchingFallbacks += "sdk"
        }
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles("proguard-gradle.txt", "proguard-gradle-repackage.txt")
            signingConfig = signingConfigs.getByName("release")
            matchingFallbacks += "odk"
        }
    }

    flavorDimensions += "application"

    productFlavors {
        supportedFlavors.forEach { (flavorName, isDefaultFlavor) ->
            create(flavorName) {
                isDefault = isDefaultFlavor
                dimension = "application"
                if (flavorName != "civ" && flavorName != "mil") {
                    applicationIdSuffix = ".$flavorName"
                }
                matchingFallbacks += "civ"
                val apiFlavor = if (flavorName == "gov") "CIV" else flavorName.uppercase()
                manifestPlaceholders["atakApiVersion"] = "com.atakmap.app@$atakVersion.$apiFlavor"
            }
        }
    }

    // Required for plugin loading: extract native libraries from the APK.
    packaging {
        jniLibs.useLegacyPackaging = true
        jniLibs.pickFirsts += listOf(
            "META-INF/INDEX.LIST",
            "META-INF/DEPENDENCIES",
            "META-INF/io.netty.versions.properties",
        )
        resources.excludes += "META-INF/INDEX.LIST"
    }

    sourceSets {
        getByName("main") {
            // Kotlin sources live in src/main/kotlin (registered by the
            // kotlin-android plugin); there is no src/main/java.
            jniLibs.srcDirs("src/main/libs")
        }
        // Move build types out of src/<type> to avoid clashing with src/.
        getByName("debug") { setRoot("build-types/debug") }
        getByName("release") { setRoot("build-types/release") }
    }

    defaultConfig {
        minSdk = 26
        targetSdk = 36
        versionCode = pluginVersionCode
        versionName = "$pluginVersion ($gitVersionName) - [$atakVersion]"
        resValue("string", "versionName", "$pluginVersion ($gitVersionName) - [$atakVersion]")
        buildConfigField("String", "ATAK_PACKAGE_NAME", "\"com.atakmap.app.civ\"")
    }

    lint {
        abortOnError = true
        checkReleaseBuilds = true
    }
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.JVM_17)
}

afterEvaluate {
    // rootProject.name may contain '-' (invalid in a package segment); strip it.
    val repackage = "atakplugin." + rootProject.name.replace("-", "")
    file("proguard-gradle-repackage.txt").writeText("-repackageclasses $repackage")
    runCatching {
        tasks.named<KotlinCompile>("compile${commandFlavor()}ReleaseKotlin") {
            compilerOptions.freeCompilerArgs.add("-Xsam-conversions=class")
        }
    }
}

fun commandFlavor(): String {
    val requested = gradle.startParameter.taskRequests.toString()
    val pattern = when {
        requested.contains("assemble") -> Regex("assemble(\\w+)(Release|Debug)")
        requested.contains("install") -> Regex("install(\\w+)(Release|Debug)")
        else -> Regex("generate(\\w+)(Release|Debug)")
    }
    return pattern.find(requested)?.groupValues?.get(1) ?: "Civ"
}

dependencies {
    implementation(fileTree("libs") { include("*.jar") })

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

    // Jetpack Compose + Material 3 (library versions aligned by the BOM).
    val composeBom = platform("androidx.compose:compose-bom:2025.02.00")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // Bundle lifecycle/savedstate (real default interface methods) so Compose's
    // AndroidComposeView links against them rather than ATAK's abstract copy.
    // This is load-bearing for Compose-in-ATAK; see the exclude block below.
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.4")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.4")
    implementation("androidx.savedstate:savedstate:1.2.1")

    testImplementation("junit:junit:4.13.2")
    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine")
}

configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "androidx.core" && !requested.name.contains("core-viewtree")) {
            useVersion("1.17.0")
        }
        if (requested.group == "androidx.lifecycle") useVersion("2.9.4")
        if (requested.group == "androidx.fragment") useVersion("1.8.9")
        if (requested.group == "com.squareup.okhttp3") useVersion("4.11.0")
        if (requested.group == "com.squareup.okio") useVersion("3.2.0")
    }
}

configurations.named("implementation") {
    // ATAK provides these; bundling them conflicts with the host classloader.
    exclude(mapOf("group" to "androidx.fragment", "module" to "fragment"))
    exclude(mapOf("group" to "androidx.lifecycle", "module" to "lifecycle"))
    exclude(mapOf("group" to "androidx.lifecycle", "module" to "lifecycle-process"))
    exclude(mapOf("group" to "com.squareup.okhttp3", "module" to "okhttp"))
    exclude(mapOf("group" to "com.squareup.okio", "module" to "okio"))
    // NOTE: androidx.lifecycle / androidx.savedstate are intentionally bundled
    // (pulled by Compose). ATAK's runtime DefaultLifecycleObserver.onCreate is
    // abstract, so Compose's AndroidComposeView must link the plugin's own copy.
    // NOTE: androidx.core (core / core-ktx) is NOT excluded. Compose UI depends on
    // it, and Android Studio's @Preview renderer (LayoutLib) instantiates the
    // composables without ATAK on the classpath, so it needs core to be bundled or
    // previews fail with NoClassDefFoundError. The plugin classloader is
    // parent-first, so at runtime ATAK's core wins and the bundled copy is inert.
}
