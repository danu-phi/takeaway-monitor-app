// Project level build.gradle.kts

plugins {
    id("com.android.application") version "8.2.2" apply false
    kotlin("android") version "1.9.24" apply false
    kotlin("jvm") version "1.9.24" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
    id("com.google.gms.google-services") version "4.4.4" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}