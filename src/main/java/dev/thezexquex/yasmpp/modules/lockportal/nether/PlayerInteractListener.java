package dev.thezexquex.yasmpp.modules.lockportal.nether;

import dev.thezexquex.yasmpp.YasmpPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

/**
 * Places frame blocks when a player clicks a portal hologram with the matching block.
 */
public class PlayerInteractListener implements Listener {
    private final YasmpPlugin plugin;

    public PlayerInteractListener(YasmpPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteractAtEntity(PlayerInteractAtEntityEvent event) {
        // Fired once per hand, but only the main hand item is consumed
        if (event.getHand() != EquipmentSlot.HAND) return;

        var manager = plugin.netherPortalManager();
        var index = manager.portalRenderer().blockIndex(event.getRightClicked());
        if (index.isEmpty()) return;

        event.setCancelled(true);

        var player = event.getPlayer();
        var result = manager.placeBlock(player, index.getAsInt());
        if (result == NetherPortalManager.PlaceResult.PLACED || result == NetherPortalManager.PlaceResult.PORTAL_COMPLETED) {
            var blockLoc = event.getRightClicked().getLocation();
            player.playSound(blockLoc, "minecraft:block.end_portal_frame.fill", 1, 0.1f);
            player.playSound(blockLoc, "minecraft:block.amethyst_block.place", 1, 2f);
            player.playSound(blockLoc, "minecraft:block.amethyst_block.place", 1, 1.7f);
        }
    }
}
