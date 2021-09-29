import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.minecraftforge.gradle.userdev.UserDevExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm")
    id("net.minecraftforge.gradle")
    id("com.github.johnrengelman.shadow")
}

group = "net.eve0415.mc"
version = "1.0-SNAPSHOT"

java.toolchain.languageVersion.set(JavaLanguageVersion.of(8))
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = "1.8"

configure<UserDevExtension> {
    mappings("snapshot", "20180814-1.12")
}

repositories {
    maven(url = "https://www.cursemaven.com")
}

dependencies {
    "minecraft"("net.minecraftforge:forge:1.12.2-14.23.5.2855")
    api("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.5.21")
    implementation("curse.maven:gregtechce-293327:3388082")
    implementation("curse.maven:gregicality-364851:3388278")
    implementation("curse.maven:the-one-probe-245211:2667280")
}

tasks.named<ProcessResources>("processResources") {
    inputs.property("version", project.version)
    from(sourceSets.main.get().resources.srcDirs) {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        include("mcmod.info")
        expand("version" to project.version)
    }
}

tasks.named<Jar>("jar") {
    manifest {
        attributes(
            mapOf(
                "FMLCorePluginContainsFMLMod" to "true",
                "FMLCorePlugin" to "net.eve0415.mc.gregpatcher.GregPatcher"
            )
        )
    }

    finalizedBy("reobfJar")
}

tasks.named<ShadowJar>("shadowJar") {
    archiveBaseName.set("GregPatcher")
    exclude("**/module-info.class")
    minimize()

    dependencies {
        include(dependency("org.jetbrains.kotlin:kotlin-stdlib"))
    }
}

tasks.named("build") { dependsOn("shadowJar") }
