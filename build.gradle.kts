import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.minecraftforge.gradle.common.tasks.SignJar
import net.minecraftforge.gradle.userdev.UserDevExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.*

plugins {
    java
    kotlin("jvm")
    id("net.minecraftforge.gradle")
    id("net.kyori.blossom")
    id("com.github.johnrengelman.shadow")
}

group = "net.eve0415.mc"
version = "1.0.0"

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = "1.8"
java.toolchain.languageVersion.set(JavaLanguageVersion.of(8))

val signProps = if (!System.getenv("KEY_STORE").isNullOrEmpty()) {
    System.getenv("KEY_STORE").reader().let {
        val prop = Properties()
        prop.load(it)
        return@let prop
    }
} else if (file("secret.properties").exists()) {
    file("secret.properties").inputStream().let {
        val prop = Properties()
        prop.load(it)
        return@let prop
    }
} else {
    Properties()
}

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

blossom {
    replaceToken("@VERSION@", project.version)
    replaceToken("@FINGERPRINT@", signProps["signSHA1"])
}

reobf.create("shadowJar")

tasks {
    compileJava {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }

    compileKotlin {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }

    named<ProcessResources>("processResources") {
        inputs.property("version", project.version)
        from(sourceSets.main.get().resources.srcDirs) {
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
            include("mcmod.info")
            expand("version" to project.version)
        }
    }

    named<Jar>("jar") {
        manifest {
            attributes(
                mapOf(
                    "FMLCorePluginContainsFMLMod" to "true",
                    "FMLCorePlugin" to "net.eve0415.mc.gregpatcher.GregPatcher"
                )
            )
        }
    }

    named<ShadowJar>("shadowJar") {
        archiveFileName.set("GregPatcher-1.12.2-${project.version}.jar")
        exclude("**/module-info.class")
        minimize()
        finalizedBy("reobfShadowJar")

        dependencies {
            include(dependency("org.jetbrains.kotlin:kotlin-stdlib"))
        }
    }

    create<SignJar>("signJar") {
        dependsOn("reobfShadowJar")
        onlyIf {
            signProps.isNotEmpty()
        }

        keyStore.set(signProps["keyStore"] as String)
        storePass.set(signProps["keyStorePass"] as String)
        alias.set(signProps["keyStoreAlias"] as String)
        keyPass.set(signProps["keyStoreKeyPass"] as String)
        inputFile.set(named<Jar>("shadowJar").get().archiveFile)
        outputFile.set(named<Jar>("shadowJar").get().archiveFile)
    }

    named("build") {
        dependsOn("signJar")
    }
}
