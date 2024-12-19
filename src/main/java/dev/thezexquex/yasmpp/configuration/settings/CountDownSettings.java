package dev.thezexquex.yasmpp.configuration.settings;

import de.unknowncity.astralib.libs.com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CountDownSettings {
    @JsonProperty
    private List<CountDownEntry> restartCountDown = List.of(
            new CountDownEntry(600, true, false, true, "entity.experience_orb.pickup"),
            new CountDownEntry(300, true, false, true, "entity.experience_orb.pickup"),
            new CountDownEntry(120, true, false, true, "entity.experience_orb.pickup"),
            new CountDownEntry(60, true, true, true, "entity.experience_orb.pickup"),
            new CountDownEntry(30, true, true, true, "entity.experience_orb.pickup"),
            new CountDownEntry(15, true, true, true, "entity.experience_orb.pickup"),
            new CountDownEntry(10, true, true, true, "entity.experience_orb.pickup"),
            new CountDownEntry(5, true, true, true, "entity.experience_orb.pickup"),
            new CountDownEntry(4, true, true, true, "entity.experience_orb.pickup"),
            new CountDownEntry(3, true, true, true, "entity.experience_orb.pickup"),
            new CountDownEntry(2, true, true, true, "entity.experience_orb.pickup"),
            new CountDownEntry(1, true, true, true, "entity.experience_orb.pickup"),
            new CountDownEntry(0, false, false, true, "block.end_portal.spawn")

    );

    @JsonProperty
    private List<CountDownEntry> gameStartCountDown = List.of(
            new CountDownEntry(15, true, true, true, "entity.experience_orb.pickup"),
            new CountDownEntry(10, true, true, true, "entity.experience_orb.pickup"),
            new CountDownEntry(5, true, true, true, "entity.experience_orb.pickup"),
            new CountDownEntry(4, true, true, true, "entity.experience_orb.pickup"),
            new CountDownEntry(3, true, true, true, "entity.experience_orb.pickup"),
            new CountDownEntry(2, true, true, true, "entity.experience_orb.pickup"),
            new CountDownEntry(1, true, true, true, "entity.experience_orb.pickup"),
            new CountDownEntry(0, false, false, true, "block.end_portal.spawn")
    );

    @JsonProperty
    private List<CountDownEntry> teleportCountDown = List.of(
            new CountDownEntry(5, false, true, true, "entity.experience_orb.pickup"),
            new CountDownEntry(4, false, true, true, "entity.experience_orb.pickup"),
            new CountDownEntry(3, false, true, true, "entity.experience_orb.pickup"),
            new CountDownEntry(2, false, true, true, "entity.experience_orb.pickup"),
            new CountDownEntry(1, false, true, true, "entity.experience_orb.pickup"),
            new CountDownEntry(0, false, false, true, "item.chorus_fruit.teleport")
    );

    @JsonProperty
    private List<CountDownEntry> openEndCountDown = List.of(
            new CountDownEntry(15, true, true, true, "entity.experience_orb.pickup"),
            new CountDownEntry(10, true, true, true, "entity.experience_orb.pickup"),
            new CountDownEntry(5, true, true, true, "entity.experience_orb.pickup"),
            new CountDownEntry(4, true, true, true, "entity.experience_orb.pickup"),
            new CountDownEntry(3, true, true, true, "entity.experience_orb.pickup"),
            new CountDownEntry(2, true, true, true, "entity.experience_orb.pickup"),
            new CountDownEntry(1, true, true, true, "entity.experience_orb.pickup"),
            new CountDownEntry(0, false, false, true, "block.end_portal.spawn")
    );

    public CountDownSettings() {

    }

    public List<CountDownEntry> restartCountDown() {
        return restartCountDown;
    }

    public List<CountDownEntry> gameStartCountDown() {
        return gameStartCountDown;
    }

    public List<CountDownEntry> teleportCountDown() {
        return teleportCountDown;
    }

    public List<CountDownEntry> openEndCountDown() {
        return openEndCountDown;
    }
}
