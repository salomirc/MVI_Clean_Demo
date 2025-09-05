plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    //hilt and ksp(annotation processor)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    // Kotlin serialization plugin for type safe routes and navigation arguments
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.example.mvi_clean_demo"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.mvi_clean_demo"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(17)
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    //added section start
    // JSON serialization library, works with the Kotlin serialization plugin.
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.material)

    // Constraintlayout Compose
    implementation(libs.androidx.constraintlayout.compose)

    // Hilt
    ksp(libs.androidx.hilt.android.compiler)
    implementation(libs.androidx.hilt)
    implementation(libs.androidx.hilt.navigation.compose)

    // Retrofit
    implementation(libs.androidx.retrofit)
    implementation(libs.androidx.gson.converter)
    implementation(libs.androidx.logging.interceptor)

    // Room
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room)
    implementation(libs.androidx.room.kotlin)
    //added section end

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)


    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    //added section start
    //Writing and executing Unit Tests on the JUnit5 Platform
    testImplementation(libs.junit.jupiter)
    // (Optional) If you need JUnit5 "Parameterized Tests"
    testImplementation(libs.junit.jupiter.params)
    // AssertJ Library
    testImplementation(libs.assertj.core)
    // Mockk Library
    testImplementation(libs.mockk)
    //added section end

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
