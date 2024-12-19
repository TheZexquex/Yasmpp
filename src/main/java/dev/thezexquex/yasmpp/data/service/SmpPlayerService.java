package dev.thezexquex.yasmpp.data.service;

import dev.thezexquex.yasmpp.YasmpPlugin;
import dev.thezexquex.yasmpp.data.entity.SmpPlayer;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class SmpPlayerService {

    private final YasmpPlugin plugin;
    private final Set<SmpPlayer> players;

    public SmpPlayerService(YasmpPlugin plugin) {
        this.players = new HashSet<>();
        this.plugin = plugin;
    }

    public void addSmpPlayer(Player player) {
        var smpPlayer = new SmpPlayer(player, plugin.homeService());
        players.add(smpPlayer);
        smpPlayer.loadHomes();
    }

    public Optional<SmpPlayer> getSmpPlayer(Player player) {
        return players.stream().filter(smpPlayer -> smpPlayer.toBukkitPlayer().getUniqueId().equals(player.getUniqueId())).findFirst();
    }
}
