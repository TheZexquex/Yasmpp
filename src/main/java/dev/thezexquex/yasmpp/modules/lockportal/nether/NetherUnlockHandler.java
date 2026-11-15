package dev.thezexquex.yasmpp.modules.lockportal.nether;

import dev.thezexquex.yasmpp.YasmpPlugin;
import net.kyori.adventure.title.Title;
import org.bukkit.Location;
import org.spongepowered.configurate.NodePath;

import java.time.Duration;

/**
 * Lights the portal and unlocks the nether once the portal is complete.
 */
public class NetherUnlockHandler implements PortalProgressListener {
    private final YasmpPlugin plugin;

    public NetherUnlockHandler(YasmpPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onCompleted(Location origin, PortalBlueprint blueprint) {
        PortalBlocks.lightPortal(origin, blueprint);

        plugin.getServer().getOnlinePlayers().forEach(player -> {
            player.playSound(player, "minecraft:entity.wither.death", 1.0f, 0.1f);
            plugin.messenger().sendTitle(player,
                    NodePath.path("event", "nether", "unlock", "title"),
                    NodePath.path("event", "nether", "unlock", "subtitle"),
                    Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(3), Duration.ofSeconds(3)));
        });

        plugin.configuration().main().general().portals().lockNether(false);
        plugin.configuration().save();
    }
}
