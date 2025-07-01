plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}


android {
    namespace = "com.example.skulfulharmony"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.skulfulharmony"
        minSdk = 29
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("androidx.recyclerview:recyclerview:1.3.1")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    //implementation ("androidx.core:core:1.7.0")
    implementation ("androidx.recyclerview:recyclerview:1.3.2")
    implementation ("androidx.core:core:1.10.1")
    implementation(libs.firebase.crashlytics.buildtools)
    implementation ("androidx.work:work-runtime:2.7.0")
    implementation("androidx.core:core-ktx:1.16.0")

//    implementation ("androidx.media3:media3-exoplayer:1.2.0")
//    implementation ("androidx.media3:media3-ui:1.2.0")
    implementation(files("../app/libs/TarsosDSP-Android-2.4.jar"))
    implementation(libs.androidx.appcompat)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-messaging:23.4.0")
    implementation ("com.google.firebase:firebase-firestore:24.0.0")


    implementation("com.github.bumptech.glide:glide:4.14.2")
    annotationProcessor("com.github.bumptech.glide:compiler:4.14.2")
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")
    implementation ("com.dropbox.core:dropbox-core-sdk:3.1.5")
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")

    implementation("commons-net:commons-net:3.8.0")

    implementation("com.google.code.gson:gson:2.10.1")
    implementation ("androidx.media3:media3-exoplayer:1.3.1")
    implementation ("androidx.media3:media3-ui:1.3.1")

    implementation ("com.squareup.okhttp3:okhttp:4.12.0")


}