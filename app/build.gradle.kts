plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.skulfulharmony"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.skulfulharmony"
        minSdk = 28
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("androidx.recyclerview:recyclerview:1.3.1")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation(libs.androidx.appcompat)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth") // Se eliminó la versión para que tome la del BOM
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.google.android.material:material:1.8.0")
    implementation("com.google.firebase:firebase-firestore") // Se eliminó la versión para que tome la del BOM
    /*implementation("com.github.bumptech.glide:glide:4.14.2") // Actualizado a una versión más reciente
    annotationProcessor("com.github.bumptech.glide:compiler:4.14.2") // Actualizado a la versión correspondiente
    implementation("commons-net:commons-net:3.8.0")
    implementation("org.apache.httpcomponents:httpclient:4.5.13") // Puedes actualizar si lo deseas, pero esta es la versión que tienes
    //implementation("com.arthenica:ffmpeg-kit-full:5.1.1")*/


}