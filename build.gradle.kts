import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
}

group = "dev.thezexquex"
version = "1.0-SNAPSHOT"

val mainClass = "${group}.${rootProject.name.lowercase()}.YasmpPlugin"
val shadeBasePath = "${group}.${rootProject.name.lowercase()}.libs."

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    implementation("cloud.commandframework", "cloud-paper", "1.8.4")
    implementation("de.chojo.sadu", "sadu-sqlite", "1.4.0")
    implementation("org.spongepowered", "configurate-yaml", "4.1.2")
    implementation("org.spongepowered", "configurate-hocon", "4.1.2")

    compileOnly("io.papermc.paper", "paper-api", "1.20.4-R0.1-SNAPSHOT")
    compileOnly("me.clip", "placeholderapi", "2.11.5")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

bukkit {

    name = "Yasmpp"
    version = "0.1.0"
    description = "Yet another smp plugin"
    author = "TheZexquex"

    main = mainClass

    foliaSupported = false

    apiVersion = "1.20"

    load = BukkitPluginDescription.PluginLoadOrder.STARTUP

    depend = listOf("PlaceholderAPI")

    defaultPermission = BukkitPluginDescription.Permission.Default.OP
}


java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(20))
}

tasks {
    shadowJar {
        relocate("org.spongepowered", shadeBasePath + "configurate")
        relocate("de.chojo.sadu", shadeBasePath + "sadu")
        relocate("cloud.commandframework", shadeBasePath + "cloud")
    }

    compileJava {
        options.encoding = "UTF-8"
    }


    jar {
        archiveBaseName.set(rootProject.name)
        archiveVersion.set(rootProject.version.toString())
    }

    register<Copy>("copyToServer") {
        val path = System.getenv("SERVER_DIR")
        if (path.toString().isEmpty()) {
            println("No SERVER_DIR env variable set")
            return@register
        }
        from(shadowJar)
        destinationDir = File(path.toString())
    }
}