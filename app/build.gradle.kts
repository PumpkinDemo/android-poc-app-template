plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "ave.mujica.poc"
    compileSdk = 36 

    defaultConfig {
        applicationId = "ave.mujica.poc"
        minSdk = 30
        targetSdk = 36
        versionCode = 1001
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    // implementation libs.appcompat
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    // testImplementation(libs.junit)
    // androidTestImplementation(libs.ext.junit)
    // androidTestImplementation(libs.espresso.core)
}