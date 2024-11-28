import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.3"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    id("xyz.jpenilla.run-paper") version "2.3.0"
}

group = "dev.thezexquex"
version = "0.2.0"

val mainClass = "${group}.${rootProject.name.lowercase()}.YasmpPlugin"
val shadeBasePath = "${group}.${rootProject.name.lowercase()}.libs."

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.xenondevs.xyz/releases")
    maven("https://repo.unknowncity.de/snapshots")
}

dependencies {
    compileOnly("de.chojo.sadu", "sadu", "2.3.0")
    implementation("xyz.xenondevs.invui", "invui", "1.41")

    compileOnly("io.papermc.paper", "paper-api", "1.21.3-R0.1-SNAPSHOT")
    compileOnly("me.clip", "placeholderapi", "2.11.5")
    compileOnly("de.unknowncity.astralib", "astralib-paper-api", "0.5.0-SNAPSHOT")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

bukkit {

    name = "Yasmpp"
    version = rootProject.version.toString()
    description = "Yet another smp plugin"
    author = "TheZexquex"

    main = mainClass

    foliaSupported = false

    apiVersion = "1.21"

    load = BukkitPluginDescription.PluginLoadOrder.POSTWORLD

    softDepend = listOf("PlaceholderAPI", "My_Worlds")
    depend = listOf("AstraLib")

    defaultPermission = BukkitPluginDescription.Permission.Default.OP
}


java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
    shadowJar {
        fun relocateDependency(from : String) = relocate(from, "$shadeBasePath$from")

        relocateDependency("xyz.xenondevs")
        relocateDependency("org.jetbrains")
        relocateDependency("org.intellij")
    }

    compileJava {
        options.encoding = "UTF-8"
    }

    jar {
        archiveBaseName.set(rootProject.name)
        archiveVersion.set(rootProject.version.toString())
    }

    runServer {
        minecraftVersion("1.21.3")

        downloadPlugins {
            // ADD plugins needed for testing
            //url("https://github.com/EssentialsX/Essentials/releases/download/2.20.1/EssentialsX-2.20.1.jar")
            //url("https://ci.unknowncity.de/job/AstraLib/37/artifact/astralib-paper-plugin/build/libs/AstraLib-Paper-0.5.0-SNAPSHOT-%2337.jar")
        }

        jvmArgs("-Dcom.mojang.eula.agree=true")
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