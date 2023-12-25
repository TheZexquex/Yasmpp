package dev.thezexquex.yasmpp;

import dev.thezexquex.yasmpp.core.configuration.ConfigurationLoader;
import dev.thezexquex.yasmpp.core.configuration.Configuration;
import dev.thezexquex.yasmpp.core.hooks.PluginHookService;
import dev.thezexquex.yasmpp.core.message.Messenger;
import dev.thezexquex.yasmpp.modules.blockdamage.ExplosionBlockDamageListener;
import dev.thezexquex.yasmpp.modules.mobileworkstations.WorkstationInteractListener;
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

    private void registerListeners() {
        var pluginManager = this.getServer().getPluginManager();

        pluginManager.registerEvents(new WorkstationInteractListener(this), this);
        pluginManager.registerEvents(new ExplosionBlockDamageListener(this), this);
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
