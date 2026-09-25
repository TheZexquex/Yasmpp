import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    id("java")
    alias(libs.plugins.shadow)
    alias(libs.plugins.plugin.yml.bukkit)
    alias(libs.plugins.run.paper)
    alias(libs.plugins.axion.release)
}

scmVersion {
    tag.prefix = "v"
    snapshotCreator { _, _ -> "-SNAPSHOT" }
}

group = "dev.thezexquex"
version = scmVersion.version

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
    implementation(libs.invui)

    compileOnly(libs.paper.api) {
        // vulnerable transitive dependencies
        exclude("org.codehaus.plexus")
        exclude("org.apache.commons", "commons-lang3")
    }
    compileOnly(libs.placeholderapi)
    compileOnly(libs.astralib)
    compileOnly(libs.plan)
    compileOnly(libs.discordsrv)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
}

bukkit {

    name = "Yasmpp"
    version = rootProject.version.toString()
    description = "Yet another smp plugin"
    author = "TheZexquex"

    main = mainClass

    foliaSupported = false

    apiVersion = "26.2"

    load = BukkitPluginDescription.PluginLoadOrder.POSTWORLD

    softDepend = listOf("PlaceholderAPI", "My_Worlds")
    depend = listOf("AstraLib", "Plan", "DiscordSRV")

    defaultPermission = BukkitPluginDescription.Permission.Default.OP
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
        minecraftVersion("26.2")

        downloadPlugins {
            // ADD plugins needed for testing
            modrinth("plan", "5.8+build.3638")
            modrinth("PlaceholderAPI", "pIvQcXW8")
            modrinth("DiscordSRV", "1.30.5")
            modrinth("FastAsyncWorldEdit", "2.15.4")
            github("UnknownCityMC", "AstraLib", "v0.8.0", "AstraLib-Paper-0.8.0-SNAPSHOT.jar")
        }

        jvmArgs("-Dcom.mojang.eula.agree=true")
    }

    register<Copy>("copyToServer") {
        description = "Copies the jar to the server directory"
        val path = System.getenv("SERVER_DIR")
        if (path.toString().isEmpty()) {
            println("No SERVER_DIR env variable set")
            return@register
        }
        from(shadowJar)
        destinationDir = File(path.toString())
    }
}