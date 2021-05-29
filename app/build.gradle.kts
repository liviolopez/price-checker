plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    signingConfigs {
        register("release") {
            storeFile = file("../cert/cert.keystore") // <- this information must be in a secure storage (CI/CD), it was added here only for test purposes
            keyAlias = "alias-test" // <- this information must be in a secure storage (CI/CD), it was added here only for test purposes
            keyPassword = "123abc" // <- this information must be in a secure storage (CI/CD), it was added here only for test purposes
            storePassword = "123abc" // <- this information must be in a secure storage (CI/CD), it was added here only for test purposes
        }
    }

    compileSdkVersion(30)
    buildToolsVersion = "30.0.3"

    defaultConfig {
        applicationId = "com.liviolopez.pricechecker"
        minSdkVersion(23)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.liviolopez.pricechecker.AppTestRunner"
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }

        named("debug") {
            isMinifyEnabled = false
            isDebuggable = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${rootProject.ext["kotlin_version"]}")
    implementation("androidx.core:core-ktx:1.5.0")
    implementation("androidx.appcompat:appcompat:1.3.0")
    implementation("com.google.android.material:material:1.3.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${rootProject.ext["kotlin_version"]}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${rootProject.ext["kotlin_version"]}")

    val navigation_version = "2.3.5"
    implementation("androidx.navigation:navigation-fragment-ktx:${navigation_version}")
    implementation("androidx.navigation:navigation-ui-ktx:${navigation_version}")

    val retrofit_version = "2.9.0"
    implementation("com.squareup.retrofit2:retrofit:${retrofit_version}")
    implementation("com.squareup.retrofit2:converter-gson:${retrofit_version}")

    val okHttp3_version = "4.9.0"
    implementation("com.squareup.okhttp3:logging-interceptor:${okHttp3_version}")

    val lifecycle_version = "2.3.1"
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${lifecycle_version}")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:${lifecycle_version}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:${lifecycle_version}")

    val hilt_version = "2.36"
    implementation("com.google.dagger:hilt-android:${hilt_version}")
    kapt("com.google.dagger:hilt-compiler:${hilt_version}")

    val room_version = "2.3.0"
    implementation("androidx.room:room-runtime:${room_version}")
    kapt("androidx.room:room-compiler:${room_version}")
    implementation("androidx.room:room-ktx:${room_version}")

    val glide_version = "4.12.0"
    implementation("com.github.bumptech.glide:glide:${glide_version}")
    kapt("com.github.bumptech.glide:compiler:${glide_version}")

    val datastore_version = "1.0.0-beta01"
    implementation("androidx.datastore:datastore-preferences:${datastore_version}")

    val camerax_version = "1.1.0-alpha04"
    implementation("androidx.camera:camera-camera2:${camerax_version}")
    implementation("androidx.camera:camera-lifecycle:${camerax_version}")
    implementation("androidx.camera:camera-view:1.0.0-alpha24")

    val mlkit_version = "16.1.1"
    implementation("com.google.mlkit:barcode-scanning:${mlkit_version}")

    // ---------------------------------------------
    // --------------------TEST --------------------

    val junit_version = "4.13.2"
    val espresso_version = "3.3.0"
    testImplementation("junit:junit:${junit_version}")
    androidTestImplementation("junit:junit:${junit_version}")
    androidTestImplementation("androidx.test:core-ktx:1.3.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:${espresso_version}")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:${espresso_version}")

    androidTestImplementation("com.squareup.okhttp3:mockwebserver:${okHttp3_version}")
    debugImplementation("androidx.fragment:fragment-testing:1.3.4")

    androidTestImplementation("com.google.dagger:hilt-android-testing:${hilt_version}")
    kaptAndroidTest("com.google.dagger:hilt-android-compiler:${hilt_version}")
}