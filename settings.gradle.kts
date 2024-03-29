pluginManagement {
    repositories {
        mavenCentral()
        maven(url = "https://maven.minecraftforge.net")
        maven(url = "https://plugins.gradle.org/m2/")
    }
    resolutionStrategy {
        eachPlugin {
            when (requested.id.toString()) {
                "org.jetbrains.kotlin.jvm" -> useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
                "net.minecraftforge.gradle" -> useModule("net.minecraftforge.gradle:ForgeGradle:5.1.+")
                "net.kyori.blossom" -> useModule("net.kyori:blossom:1.3.0")
                "com.github.johnrengelman.shadow" -> useModule("gradle.plugin.com.github.johnrengelman:shadow:7.1.2")
            }
        }
    }
}
