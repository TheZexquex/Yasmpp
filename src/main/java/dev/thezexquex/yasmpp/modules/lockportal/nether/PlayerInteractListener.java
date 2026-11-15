package dev.thezexquex.yasmpp.modules.lockportal.nether;

import dev.thezexquex.yasmpp.YasmpPlugin;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class PlayerInteractListener implements Listener {
    private static final String SCAN_PERMISSION = "yasmpp.command.portal";

    private final YasmpPlugin plugin;

    public PlayerInteractListener(YasmpPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteractAtEntity(PlayerInteractAtEntityEvent event) {
        // Fired once per hand, but only the main hand item is consumed
        if (event.getHand() != EquipmentSlot.HAND) return;

        var clicked = event.getRightClicked();
        if (clicked.getType() != EntityType.INTERACTION) return;

        var portalRenderer = plugin.netherPortalManager().portalRenderer();
        if (portalRenderer == null || !portalRenderer.isPortalBlock(clicked)) return;

        event.setCancelled(true);

        var player = event.getPlayer();
        var handItem = player.getInventory().getItemInMainHand();
        var portalBlock = portalRenderer.getPortalBlock(clicked);
        if (handItem.getType() != portalBlock.getMaterial()) return;

        if (player.getGameMode() != GameMode.CREATIVE) {
            handItem.setAmount(handItem.getAmount() - 1);
        }

        var block = clicked.getLocation().getBlock();
        var blockData = portalBlock.getMaterial().createBlockData();
        if (portalBlock.getBlockData() != null) {
            try {
                blockData = plugin.getServer().createBlockData(portalBlock.getBlockData());
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Could not parse block data: " + portalBlock.getBlockData());
            }
        }
        block.setBlockData(blockData);

        portalRenderer.completePortalBlock(clicked);

        var blockLoc = block.getLocation();
        player.playSound(blockLoc, "minecraft:block.end_portal_frame.fill", 1, 0.1f);
        player.playSound(blockLoc, "minecraft:block.amethyst_block.place", 1, 2f);
        player.playSound(blockLoc, "minecraft:block.amethyst_block.place", 1, 1.7f);

        plugin.portalConfiguration().save();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) {
            return;
        }

        if (event.getItem() == null || event.getItem().getType() != Material.STRUCTURE_VOID) {
            return;
        }

        var player = event.getPlayer();
        if (!player.hasPermission(SCAN_PERMISSION)) {
            return;
        }

        var scanSession = plugin.netherPortalManager().scanSession(player);
        var location = event.getClickedBlock().getLocation();

        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            scanSession.pos1(location);
            player.sendMessage(Component.text("Set position 1 to: " + location));
        }
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            scanSession.pos2(location);
            player.sendMessage(Component.text("Set position 2 to: " + location));
        }

        event.setCancelled(true);

        plugin.netherPortalManager().tryScanPortal(player);
    }
}
