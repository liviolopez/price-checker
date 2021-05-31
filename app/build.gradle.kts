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

    compileSdkVersion(Build.compileSdk)
    buildToolsVersion = Build.toolsVersion

    defaultConfig {
        applicationId = "com.liviolopez.pricechecker"

        minSdkVersion(Build.minSdk)
        targetSdkVersion(Build.targetSdk)

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
    implementation(Dep.Kotlin.StdLib)
    implementation(Dep.Kotlin.Coroutines)
    implementation(Dep.Kotlin.Reflect)

    implementation(Dep.CoreKtx)
    implementation(Dep.AppCompat)
    implementation(Dep.Material)
    implementation(Dep.ConstraintLayout)

    implementation(Dep.GoogleMLKit)
    implementation(Dep.DatastorePreferences)
    implementation(Dep.Okhttp3)

    implementation(Dep.Navigation.Fragment)
    implementation(Dep.Navigation.UI)

    implementation(Dep.Retrofit2.Retrofit)
    implementation(Dep.Retrofit2.Gson)

    implementation(Dep.Lifecycle.ViewModel)
    implementation(Dep.Lifecycle.LiveData)
    implementation(Dep.Lifecycle.SavedState)

    implementation(Dep.Hilt.Android)
    kapt(Dep.Hilt.Compiler)

    implementation(Dep.Room.Runtime)
    implementation(Dep.Room.Ktx)
    kapt(Dep.Room.Compiler)

    implementation(Dep.Glide.Glide)
    kapt(Dep.Glide.Compiler)

    implementation(Dep.Camera.Camera2)
    implementation(Dep.Camera.Lifecycle)
    implementation(Dep.Camera.View)


    // -----------------------------------------------------------------
    // ------------------------------TEST ------------------------------
    // -----------------------------------------------------------------


    testImplementation(Dep.Test.JUnit.JUnit)
    androidTestImplementation(Dep.Test.JUnit.JUnit)
    androidTestImplementation(Dep.Test.JUnit.Ext)

    androidTestImplementation(Dep.Test.Espresso.Core)
    androidTestImplementation(Dep.Test.Espresso.Contrib)

    androidTestImplementation(Dep.Test.CoreKtx)
    androidTestImplementation(Dep.Test.Okhttp3)
    debugImplementation(Dep.Test.Fragment)

    androidTestImplementation(Dep.Test.Hilt.Testing)
    kaptAndroidTest(Dep.Test.Hilt.Compiler)
}
