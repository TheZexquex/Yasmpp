package dev.thezexquex.yasmpp.util;

import org.bukkit.entity.Player;

public class PlayerProgressUtil {

    public static void revokeAllAdvancements(Player player) {
        player.getServer().advancementIterator().forEachRemaining(advancement -> {
            player.getAdvancementProgress(advancement).getAwardedCriteria().forEach(string -> {
                player.getAdvancementProgress(advancement).revokeCriteria(string);
            });
        });
    }

    public static void revokeAllRecipes(Player player) {
        player.undiscoverRecipes(player.getDiscoveredRecipes());
    }

    public static void clearInventory(Player player) {
        player.getInventory().clear();
    }

    public static void clearEnderChest(Player player) {
        player.getEnderChest().clear();
    }
}
