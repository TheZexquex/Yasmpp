package dev.thezexquex.yasmpp.modules.lockportal.nether;

import de.unknowncity.astralib.paper.api.inventory.InventoryUtil;
import dev.thezexquex.yasmpp.YasmpPlugin;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Interaction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class PlayerInteractListener implements Listener {
    private final YasmpPlugin plugin;

    public PlayerInteractListener(YasmpPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteractAtEntity(PlayerInteractAtEntityEvent event) {
        var clicked = event.getRightClicked();
        var handItem = event.getPlayer().getInventory().getItemInMainHand();
        if (clicked.getType() != EntityType.INTERACTION) return;

        var portalRenderer = plugin.netherPortalManager().portalRenderer();
        if (portalRenderer == null) return;

        if (portalRenderer.isPortalBlock(clicked)) {
            var portalBlock = portalRenderer.getPortalBlock(clicked);

            if (handItem.getType() == portalBlock.getMaterial()) {
                handItem.setAmount(handItem.getAmount() - 1);
                var blockLoc = clicked.getLocation().getBlock().getLocation();
                blockLoc.getBlock().setType(portalBlock.getMaterial());

                portalRenderer.completePortalBlock(clicked);

                event.getPlayer().playSound(blockLoc, "minecraft:block.end_portal_frame.fill", 1, 0.1f);
                event.getPlayer().playSound(blockLoc, "minecraft:block.amethyst_block.place", 1, 2f);
                event.getPlayer().playSound(blockLoc, "minecraft:block.amethyst_block.place", 1, 1.7f);

                plugin.portalConfiguration().save();
            }
        }
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

        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            plugin.netherPortalManager().scanSession().pos1(event.getClickedBlock().getLocation());
            player.sendMessage(Component.text("Set position 1 to: " + event.getClickedBlock().getLocation()));
        }
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            plugin.netherPortalManager().scanSession().pos2(event.getClickedBlock().getLocation());
            player.sendMessage(Component.text("Set position 2 to: " + Objects.requireNonNull(event.getClickedBlock().getLocation())));
        }

        event.setCancelled(true);

        plugin.netherPortalManager().tryScanPortal(player);
    }
}