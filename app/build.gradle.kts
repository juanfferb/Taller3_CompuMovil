buildscript {
    repositories {
        google()
    }
    dependencies {
        classpath("com.google.gms:google-services:4.4.1")
    }
}

plugins {
    id("com.android.application")

}

android {
    namespace = "com.example.taller3_compumovil"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.taller3_compumovil"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation ("androidx.core:core-ktx:version")
    implementation ("androidx.appcompat:appcompat:version")
    implementation ("com.google.android.material:material:version")
    implementation ("androidx.activity:activity:version")
    implementation ("androidx.constraintlayout:constraintlayout:version")
    implementation ("com.google.firebase:firebase-database-ktx:version")
    implementation ("com.google.firebase:firebase-database:version")
    implementation ("com.google.firebase:firebase-storage:version")
    implementation (platform("com.google.firebase:firebase-bom:version"))
    implementation ("com.google.firebase:firebase-auth:version")
    implementation ("androidx.media3:media3-common:version")
    implementation ("com.google.android.gms:play-services-maps:version")
    implementation ("com.google.android.gms:play-services-location:version")
    implementation("com.google.firebase:firebase-auth-ktx:22.3.1")
    testImplementation ("junit:junit:version")
    androidTestImplementation ("androidx.test.ext:junit:version")
    androidTestImplementation ("androidx.test.espresso:espresso-core:version")
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")
}
