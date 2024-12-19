package dev.thezexquex.yasmpp.modules.spawnelytra.integration;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

public class FJetpack2Reloaded {

    public static boolean wearsJetpack(Player player) {
        var jetPackItem = player.getInventory().getChestplate();

        if (jetPackItem == null) {
            return false;
        }

        var jetPackKey = NamespacedKey.fromString("fjetpack2reloaded:fj2ractive");

        if (jetPackKey == null) {
            return false;
        }

        return jetPackItem.getPersistentDataContainer().has(jetPackKey);
    }
}
