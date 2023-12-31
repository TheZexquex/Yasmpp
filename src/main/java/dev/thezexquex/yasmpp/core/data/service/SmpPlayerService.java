package dev.thezexquex.yasmpp.core.data.service;

import dev.thezexquex.yasmpp.player.SmpPlayer;

import java.util.HashSet;
import java.util.Set;

public class SmpPlayerService {
    private final Set<SmpPlayer> players;

    public SmpPlayerService() {
        this.players = new HashSet<>();
    }

}
