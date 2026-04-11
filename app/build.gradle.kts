plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services)
}

fun quoteBuildConfigValue(value: String): String {
    return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\""
}

android {
    namespace = "com.mindease"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.mindease"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        val chatBaseUrl = providers.gradleProperty("mindease.chat.baseUrl").orElse("").get()
        val chatApiKey = providers.gradleProperty("mindease.chat.apiKey").orElse("").get()
        val chatModel = providers.gradleProperty("mindease.chat.model").orElse("gpt-4o-mini").get()

        buildConfigField("String", "CHAT_API_BASE_URL", quoteBuildConfigValue(chatBaseUrl))
        buildConfigField("String", "CHAT_API_KEY", quoteBuildConfigValue(chatApiKey))
        buildConfigField("String", "CHAT_MODEL", quoteBuildConfigValue(chatModel))

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.fragment)
    implementation(libs.constraintlayout)
    implementation(libs.recyclerview)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.room.runtime)
    implementation(libs.mpandroidchart)
    annotationProcessor(libs.room.compiler)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
