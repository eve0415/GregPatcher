pluginManagement {
    repositories {
        mavenCentral()
        maven(url = "https://maven.minecraftforge.net")
        maven(url = "https://plugins.gradle.org/m2/")
    }
    resolutionStrategy {
        eachPlugin {
            when (requested.id.toString()) {
                "org.jetbrains.kotlin.jvm" -> useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.31")
                "net.minecraftforge.gradle" -> useModule("net.minecraftforge.gradle:ForgeGradle:5.1.+")
                "com.github.johnrengelman.shadow" -> useModule("gradle.plugin.com.github.jengelman.gradle.plugins:shadow:7.0.0")
            }
        }
    }
}