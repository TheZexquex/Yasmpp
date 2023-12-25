package dev.thezexquex.yasmpp.configuration;

import dev.thezexquex.yasmpp.YasmpPlugin;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Path;
import java.util.logging.Level;

public class ConfigManager {
    private final YasmpPlugin plugin;
    private HoconConfigurationLoader mainConfigurationLoader;
    private YamlConfigurationLoader messageConfigurationLoader;
    private ConfigurationNode mainConfigRootNode;
    private ConfigurationNode messageConfigRootNode;

    public ConfigManager(YasmpPlugin plugin) {
        this.plugin = plugin;
    }

    public void intiConfigurationLoader() {
        var mainConfigPath = Path.of("resources", "config.conf");
        mainConfigurationLoader = HoconConfigurationLoader.builder().path(mainConfigPath).build();

        var messageConfigPath = Path.of("resources", "messages.yml");
        messageConfigurationLoader = YamlConfigurationLoader.builder().path(mainConfigPath).build();

        loadConfigurationNodes(mainConfigurationLoader, messageConfigurationLoader);
    }

    private void loadConfigurationNodes(HoconConfigurationLoader mainConfigurationLoader, YamlConfigurationLoader messageConfigurationLoader) {
        try {
            mainConfigRootNode = mainConfigurationLoader.load();
        } catch (ConfigurateException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load config.conf", e);
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return;
        }

        try {
            messageConfigRootNode = messageConfigurationLoader.load();
        } catch (ConfigurateException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load messages.yml", e);
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }
    }


}
