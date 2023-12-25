package dev.thezexquex.yasmpp.core.hooks;

import org.bukkit.Server;

public abstract class Hook {
    protected abstract String getIdentifier();
    public boolean isAvailable(Server server) {
        return server.getPluginManager().isPluginEnabled(getIdentifier());
    }
}
