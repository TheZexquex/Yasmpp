package dev.thezexquex.yasmpp.modules.lockportal.nether;

import dev.thezexquex.yasmpp.YasmpPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Lets admins select two corners with a structure void to scan the portal blueprint.
 */
public class PortalScanListener implements Listener {
    private static final String SCAN_PERMISSION = "yasmpp.command.portal";

    private final YasmpPlugin plugin;
    private final Map<UUID, PortalScanSession> scanSessions = new HashMap<>();

    public PortalScanListener(YasmpPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null || event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        if (event.getItem() == null || event.getItem().getType() != Material.STRUCTURE_VOID) {
            return;
        }

        var player = event.getPlayer();
        if (!player.hasPermission(SCAN_PERMISSION)) {
            return;
        }

        var scanSession = scanSessions.computeIfAbsent(player.getUniqueId(), uuid -> new PortalScanSession(null, null));
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

        tryScanPortal(player, scanSession);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        scanSessions.remove(event.getPlayer().getUniqueId());
    }

    private void tryScanPortal(Player player, PortalScanSession scanSession) {
        var pos1 = scanSession.pos1();
        var pos2 = scanSession.pos2();
        if (pos1 == null || pos2 == null) {
            return;
        }

        var error = PortalScanner.validateSelection(pos1, pos2);
        if (error.isPresent()) {
            scanSession.pos2(null);
            player.sendMessage(Component.text(error.get()).color(NamedTextColor.RED));
            return;
        }

        var blueprint = PortalScanner.scanStructure(pos1, pos2);
        scanSessions.remove(player.getUniqueId());

        plugin.netherPortalManager().replaceBlueprint(blueprint);
        player.sendMessage(Component.text("Portal successfully scanned! " + blueprint.blocks().size() + " blocks found.").color(NamedTextColor.GREEN));
    }
}
