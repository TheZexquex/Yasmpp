package dev.thezexquex.yasmpp.modules.spawnelytra;

import dev.thezexquex.yasmpp.YasmpPlugin;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.spongepowered.configurate.NodePath;

import java.util.HashMap;
import java.util.UUID;

public class ElytraManager {
    private final HashMap<UUID, Integer> flyingPlayers;
    private final YasmpPlugin plugin;
    private final int maxBoosts;
    private ItemStack firework;

    public ElytraManager(YasmpPlugin plugin) {
        this.plugin = plugin;
        this.flyingPlayers = new HashMap<>();
        this.maxBoosts = plugin.configuration().generalSettings().generalSpawnElytraSettings().maxBoosts();
        this.firework = new ItemStack(Material.FIREWORK_ROCKET);
        var fireworkMeta = (FireworkMeta) firework.getItemMeta();
        fireworkMeta.setPower(3);
        firework.setItemMeta(fireworkMeta);
    }

    public void enableElytra(Player player) {
        flyingPlayers.put(player.getUniqueId(), maxBoosts);
        player.setFlying(false);
        player.setAllowFlight(false);
        player.setGliding(true);

        plugin.messenger().sendMessage(
                player,
                NodePath.path("event", "elytra-boost", "remaining"),
                TagResolver.resolver("boosts", Tag.preProcessParsed(String.valueOf(flyingPlayers.get(player.getUniqueId()))))
        );
    }

    public void disableElytra(Player player, boolean save) {
        if (!save) {
            player.setGliding(false);
        }
        flyingPlayers.remove(player.getUniqueId());
    }

    public boolean isFlying(Player player) {
        return flyingPlayers.containsKey(player.getUniqueId());
    }

    public int getBoostsLeft(Player player) {
        return flyingPlayers.get(player.getUniqueId());
    }

    public void boostElytra(Player player) {
        if (getBoostsLeft(player) == 0) {
            return;
        }
        flyingPlayers.put(player.getUniqueId(), getBoostsLeft(player) - 1);
        player.fireworkBoost(firework);
    }
}
