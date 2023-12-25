package dev.thezexquex.yasmpp.core.hooks;

import dev.thezexquex.yasmpp.core.hooks.externalhooks.PlaceholderApiHook;
import org.bukkit.Server;

public class PluginHookService {
    private final Server server;

    public PluginHookService(Server server) {
        this.server = server;
    }

    public boolean isPapiAvailable() {
        return new PlaceholderApiHook().isAvailable(server);
    }
}
