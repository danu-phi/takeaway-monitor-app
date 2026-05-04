// Project level build.gradle.kts

plugins {
    kotlin("multiplatform") version "1.5.30"
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
