package dev.thezexquex.yasmpp.core.configuration;

import dev.thezexquex.yasmpp.YasmpPlugin;
import dev.thezexquex.yasmpp.core.configuration.settings.*;
import dev.thezexquex.yasmpp.core.configuration.settings.general.GeneralBorderSettings;
import dev.thezexquex.yasmpp.core.configuration.settings.general.GeneralEndSettings;
import dev.thezexquex.yasmpp.core.configuration.settings.general.GeneralExplosionDamageSettings;
import dev.thezexquex.yasmpp.core.configuration.settings.general.GeneralSpawnElytraSettings;
import dev.thezexquex.yasmpp.core.configuration.typeserializer.*;
import dev.thezexquex.yasmpp.core.configuration.typeserializer.general.GeneralEndSettingsTypeSerializer;
import dev.thezexquex.yasmpp.core.configuration.typeserializer.general.GeneralBorderSettingsTypeSerializer;
import dev.thezexquex.yasmpp.core.configuration.typeserializer.general.GeneralExplosionDamageSettingsTypeSerializer;
import dev.thezexquex.yasmpp.core.configuration.typeserializer.general.GeneralSpawnElytraSettingsTypeSerializer;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Path;
import java.util.logging.Level;

public class ConfigurationLoader {
    private final YasmpPlugin plugin;
    private HoconConfigurationLoader mainConfigurationLoader;
    private YamlConfigurationLoader messageConfigurationLoader;
    private ConfigurationNode mainConfigRootNode;
    private ConfigurationNode messageConfigRootNode;

    public ConfigurationLoader(YasmpPlugin plugin) {
        this.plugin = plugin;
    }

    public void intiConfigurationLoader() {
        var mainConfigPath = plugin.getDataFolder().toPath().resolve(Path.of("config.conf"));
        mainConfigurationLoader = HoconConfigurationLoader
                .builder()
                .path(mainConfigPath)
                .defaultOptions(opts -> opts.serializers(build -> build.register(Configuration.class, new ConfigurationTypeSerializer())))
                .defaultOptions(opts -> opts.serializers(build -> build.register(ChatSettings.class, new ChatSettingsTypeSerializer())))
                .defaultOptions(opts -> opts.serializers(build -> build.register(CountDownLine.class, new CountDownLineTypeSerializer())))
                .defaultOptions(opts -> opts.serializers(build -> build.register(CountDownSettings.class, new CountDownSettingsTypeSerializer())))
                .defaultOptions(opts -> opts.serializers(build -> build.register(GeneralSettings.class, new GeneralSettingsTypeSerializer())))
                .defaultOptions(opts -> opts.serializers(build -> build.register(TeleportSettings.class, new TeleportSettingsTypeSerializer())))
                .defaultOptions(opts -> opts.serializers(build -> build.register(GeneralBorderSettings.class, new GeneralBorderSettingsTypeSerializer())))
                .defaultOptions(opts -> opts.serializers(build -> build.register(GeneralEndSettings.class, new GeneralEndSettingsTypeSerializer())))
                .defaultOptions(opts -> opts.serializers(build -> build.register(GeneralExplosionDamageSettings.class, new GeneralExplosionDamageSettingsTypeSerializer())))
                .defaultOptions(opts -> opts.serializers(build -> build.register(GeneralSpawnElytraSettings.class, new GeneralSpawnElytraSettingsTypeSerializer())))
                .build();

        var messageConfigPath = plugin.getDataFolder().toPath().resolve(Path.of("messages.yml"));
        messageConfigurationLoader = YamlConfigurationLoader.builder().path(messageConfigPath).build();
    }

    public ConfigurationNode loadConfiguration() {
        try {
            mainConfigRootNode = mainConfigurationLoader.load();
        } catch (ConfigurateException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load config.conf", e);
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }
        return mainConfigRootNode;
    }

    public ConfigurationNode loadMessageConfiguration() {
        try {
            messageConfigRootNode = messageConfigurationLoader.load();
        } catch (ConfigurateException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load messages.yml", e);
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }
        return messageConfigRootNode;
    }

    public void saveDefaultConfigs() {
        plugin.saveResource("config.conf", false);
        plugin.saveResource("messages.yml", false);
    }

    public void saveConfiguration() {
        try {
            mainConfigRootNode.set(plugin.configuration());
            mainConfigurationLoader.save(mainConfigRootNode);
        } catch (ConfigurateException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save config.conf", e);
        }
    }
}
