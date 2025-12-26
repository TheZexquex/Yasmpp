package dev.thezexquex.yasmpp.data.service;

import dev.thezexquex.yasmpp.YasmpPlugin;
import dev.thezexquex.yasmpp.data.entity.SmpPlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SmpPlayerService {

    private final YasmpPlugin plugin;
    private final Map<UUID, SmpPlayer> players = new ConcurrentHashMap<>();

    public SmpPlayerService(YasmpPlugin plugin) {
        this.plugin = plugin;
    }

    public SmpPlayer getOrCreate(Player player) {
        var smpPlayer = players.computeIfAbsent(
                player.getUniqueId(),
                uuid -> new SmpPlayer(player.getUniqueId(), plugin.homeService())
        );

        smpPlayer.loadHomeSlots();
        smpPlayer.loadHomes();

        return smpPlayer;
    }

    public Optional<SmpPlayer> get(Player player) {
        return Optional.ofNullable(players.get(player.getUniqueId()));
    }

    public Optional<SmpPlayer> get(UUID player) {
        return Optional.ofNullable(players.get(player));
    }

    public void remove(Player player) {
        players.remove(player.getUniqueId());
    }
}
