import org.gradle.internal.impldep.io.opencensus.stats.ViewData

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.project_hc002"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.project_hc002"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

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
        viewBinding = true
    }
    dataBinding {
        enable = true
    }
}

dependencies {
    implementation ("com.android.volley:volley:1.2.1")
    implementation ("com.google.firebase:firebase-messaging:23.1.2")
    implementation ("io.socket:socket.io-client:2.0.1")
    implementation ("com.google.code.gson:gson:2.8.9")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.annotation)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.swiperefreshlayout)
    implementation(libs.media3.session)
    implementation(libs.firebase.messaging)
//    implementation(libs.emoji)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

//        implementation (libs.com.arthenica.mobile.ffmpeg.full)
    // Retrofit and OkHttp dependencies
    implementation (libs.retrofit)
    implementation (libs.converter.gson)
    implementation (libs.logging.interceptor)
    implementation (libs.okhttp3.okhttp)
    implementation (libs.glide)
    implementation (libs.arthenica.mobile.ffmpeg.min)
//    implementation (libs.recaptcha)
//    implementation (libs.Swiperefreshlayout)
// Optional: Add logging interceptor for debugging
    implementation(libs.logging.interceptor)
}