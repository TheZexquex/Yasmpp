import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    id("java")
    id("com.gradleup.shadow") version "9.0.0"
    id("de.eldoria.plugin-yml.bukkit") version "0.8.0"
    id("xyz.jpenilla.run-paper") version "3.0.2"
}

group = "dev.thezexquex"
version = "0.3.0"

val mainClass = "${group}.${rootProject.name.lowercase()}.YasmpPlugin"
val shadeBasePath = "${group}.${rootProject.name.lowercase()}.libs."

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.xenondevs.xyz/releases")
    maven("https://repo.unknowncity.de/snapshots")
    maven("https://jitpack.io")
    maven("https://nexus.scarsz.me/content/groups/public/")
}

dependencies {
    implementation("xyz.xenondevs.invui", "invui", "2.0.0-alpha.20")

    compileOnly("io.papermc.paper", "paper-api", "1.21.10-R0.1-SNAPSHOT")
    compileOnly("me.clip", "placeholderapi", "2.11.6")
    compileOnly("de.unknowncity.astralib", "astralib-paper-api", "0.7.0-SNAPSHOT")
    compileOnly("com.github.plan-player-analytics:Plan:5.6.2906")
    compileOnly("com.discordsrv:discordsrv:1.28.0")

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
    depend = listOf("AstraLib", "Plan", "PlaceholderAPI", "DiscordSRV")

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
        minecraftVersion("1.21.10")

        downloadPlugins {
            // ADD plugins needed for testing
            //url("https://github.com/EssentialsX/Essentials/releases/download/2.20.1/EssentialsX-2.20.1.jar")
            //url("https://ci.unknowncity.de/job/AstraLib/37/artifact/astralib-paper-plugin/build/libs/AstraLib-Paper-0.5.0-SNAPSHOT-%2337.jar")
            //url("https://ci.unknowncity.de/job/AstraLib/56/artifact/astralib-paper-plugin/build/libs/AstraLib-Paper-0.7.0-SNAPSHOT-%2356.jar")
            url("https://github.com/plan-player-analytics/Plan/releases/download/5.7.3123/Plan-5.7-dev-build-3123.jar")
            url("https://ci.extendedclip.com/job/PlaceholderAPI/212/artifact/build/libs/PlaceholderAPI-2.11.7-DEV-212.jar")
            url("https://ci.athion.net/job/FastAsyncWorldEdit/1215/artifact/artifacts/FastAsyncWorldEdit-Paper-2.14.1-SNAPSHOT-1215.jar")
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