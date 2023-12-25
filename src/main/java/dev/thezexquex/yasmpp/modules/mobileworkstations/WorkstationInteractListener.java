package dev.thezexquex.yasmpp.modules.mobileworkstations;

import dev.thezexquex.yasmpp.YasmpPlugin;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.spongepowered.configurate.NodePath;

public class WorkstationInteractListener implements Listener {
    private final YasmpPlugin plugin;

    public WorkstationInteractListener(YasmpPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onWorkstationInteract(PlayerInteractEvent event) {
        var item = event.getItem();
        if (item == null) {
            return;
        }
        if (event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }
        var player = event.getPlayer();
        if (!player.isSneaking()) {
            return;
        }
        var location = player.getLocation();

        switch (item.getType()) {
            case CRAFTING_TABLE -> {
                player.openWorkbench(location, true);
                sendSuccessMessage(player, item.getType());
            }
            case ENCHANTING_TABLE -> {
                player.openEnchanting(location, true);
                sendSuccessMessage(player, item.getType());
            }
            case CARTOGRAPHY_TABLE -> {
                player.openCartographyTable(location, true);
                sendSuccessMessage(player, item.getType());
            }
            case GRINDSTONE -> {
                player.openGrindstone(location, true);
                sendSuccessMessage(player, item.getType());
            }
            case SMITHING_TABLE -> {
                player.openSmithingTable(location, true);
                sendSuccessMessage(player, item.getType());
            }
            case ANVIL, CHIPPED_ANVIL, DAMAGED_ANVIL -> {
                player.openAnvil(location, true);
                sendSuccessMessage(player, item.getType());
            }
            case LOOM -> {
                player.openLoom(location, true);
                sendSuccessMessage(player, item.getType());
            }
            case STONECUTTER -> {
                player.openStonecutter(location, true);
                sendSuccessMessage(player, item.getType());
            }
            case ENDER_CHEST -> {
                player.openInventory(player.getEnderChest());
                sendSuccessMessage(player, item.getType());
            }
        }
    }

    public void sendSuccessMessage(Player player, Material material) {
        plugin.messenger().messageToPlayer(
                player,
                NodePath.path("event", "open-crafting-table"),
                TagResolver.resolver("type", Tag.preProcessParsed(material.toString()))
        );
    }
}
