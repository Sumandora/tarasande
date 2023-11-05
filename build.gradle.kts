import su.mandora.codechecker.CodeChecker
import java.net.URL

plugins {
    id("fabric-loom")
    id("org.jetbrains.kotlin.jvm")
    base
}

val tarasandeName = "tarasande"
version = property("tarasande_version")!!
group = "su.mandora"

base {
    archivesName = tarasandeName
}

loom {
    accessWidenerPath = file("src/main/resources/$tarasandeName.accesswidener")

    mods {
        create(base.archivesName.get()) {
            sourceSet(sourceSets.main.get())
        }
    }
}

val dependency: Configuration by configurations.creating

configurations {
    api.get().extendsFrom(dependency)
}

allprojects {
    repositories {
        maven { url = URL("https://www.jitpack.io").toURI() }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    mappings("net.fabricmc:yarn:${property("yarn_mappings")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${property("loader_version")}")

    dependency("io.netty:netty-handler-proxy:4.1.82.Final") { // Match this version with Minecraft's Netty
        exclude("io.netty", "netty-common")
        exclude("io.netty", "netty-buffer")
        exclude("io.netty", "netty-transport")
        exclude("io.netty", "netty-codec")
        exclude("io.netty", "netty-transport-native-unix-common")
        exclude("io.netty", "netty-resolver")
        exclude("io.netty", "netty-handler")
    }

    modImplementation("net.fabricmc:fabric-language-kotlin:${property("fabric_kotlin_version")}")

    fun ExternalModuleDependency.excludeKotlinSTD() {
        exclude("org.jetbrains.kotlin")
        exclude("org.jetbrains.kotlinx")
    }

    dependency("com.github.Sumandora:MCSkinLookup:${property("mcskinlookup_commit")}") {
        excludeKotlinSTD()
    }
    dependency("com.github.Sumandora:AuthLib:${property("authlib_commit")}") {
        excludeKotlinSTD()
    }
}

tasks {
    processResources {
        inputs.property("version", project.version)

        filesMatching("fabric.mod.json") {
            expand("version" to project.version)
        }
    }

    jar {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        dependency.forEach {
            from(zipTree(it))
        }
        from("LICENSE") {
            rename { "${it}_$tarasandeName" }
        }
    }

    compileKotlin.get().kotlinOptions.jvmTarget = "17"
    compileJava.get().options.release.set(17)
}

java {
    withSourcesJar()

    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.register("checkCode") {
    group = tarasandeName
    dependsOn("build")
    doLast {
        val codeChecker = CodeChecker(sourceSets.main.get())
        codeChecker.check()
    }
}

tasks.register("installPackages") {
    group = tarasandeName
    dependsOn("build")
    doLast {
        val modFolder = File("run", "mods")
        if (!modFolder.exists()) modFolder.mkdirs()

        subprojects.filter { it.name.startsWith("package") }.forEach { p ->
            val packageName = p.name + "-" + p.version + ".jar"
            val libs = p.layout.buildDirectory.file("libs").orNull?.asFile ?: run {
                println("Failed to acquire buildDirectory for " + p.name)
                return@forEach
            }

            val build = File(libs, packageName)
            val modDest = File(modFolder, packageName)
            if (build.exists()) {
                if (modDest.exists())
                    if (modDest.delete())
                        println("Deleted old $packageName")
                    else
                        println("Failed to delete old $packageName version")

                build.copyTo(modDest)
                println("Copied $packageName")
            }
        }
    }
}
