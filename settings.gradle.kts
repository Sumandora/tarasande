pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven { url = java.net.URL("https://maven.fabricmc.net/").toURI() }
    }

    plugins {
        id("fabric-loom") version providers.gradleProperty("loom_version")
        id("org.jetbrains.kotlin.jvm") version providers.gradleProperty("kotlin_version")
    }
}

include("package-linux-platform")
include("package-mod-fixes")
include("package-third-party")
include("package-protocol-spoofer")
include("package-serverpinger")

// Uncomment if you want to see the example package
//include("package-example")

