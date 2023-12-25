package dev.thezexquex.yasmpp;

import dev.thezexquex.yasmpp.core.configuration.ConfigurationLoader;
import dev.thezexquex.yasmpp.core.configuration.Configuration;
import dev.thezexquex.yasmpp.core.hooks.PluginHookService;
import dev.thezexquex.yasmpp.core.message.Messenger;
import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.logging.Level;

public class YasmpPlugin extends JavaPlugin {
    private Configuration configuration;
    private Messenger messenger;
    private PluginHookService pluginHookService;

    @Override
    public void onEnable() {

    }

    public void reloadPlugin() {
        this.pluginHookService = new PluginHookService(this.getServer());

        var configurationLoader = new ConfigurationLoader(this);

        var configurationRootNode = configurationLoader.loadConfiguration();
        var messageRootNode = configurationLoader.loadMessageConfiguration();

        try {
            configuration = configurationRootNode.get(Configuration.class)  ;
        } catch (SerializationException e) {
            this.getLogger().log(Level.SEVERE, "Failed to load config.conf", e);
        }

        messenger = new Messenger(this, messageRootNode);
    }

    public PluginHookService pluginHookService() {
        return pluginHookService;
    }
    public Configuration configuration() {
        return configuration;
    }

    public Messenger messenger() {
        return messenger;
    }
}
