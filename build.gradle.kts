buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath(Plugins.gradle)
        classpath(Plugins.kotlin)
        classpath(Plugins.navigation)
        classpath(Plugins.dagger_hilt)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
