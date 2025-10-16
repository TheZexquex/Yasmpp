package dev.thezexquex.yasmpp.configuration.settings;

import de.unknowncity.astralib.libs.com.fasterxml.jackson.annotation.JsonProperty;
import dev.thezexquex.yasmpp.homes.HomeSlot;
import org.bukkit.Material;

import java.util.List;

public class HomeSettings {
    @JsonProperty
    private final List<HomeSlot> homeSlots = List.of(
            new HomeSlot(1, Material.DIAMOND.name(), 64),
            new HomeSlot(2, Material.DIAMOND.name(), 256),
            new HomeSlot(3, Material.DIAMOND.name(), 512),
            new HomeSlot(4, Material.DIAMOND.name(), 512)
    );

    @JsonProperty
    private final String lockedHomeSlotMaterial = Material.IRON_BARS.name();
    @JsonProperty
    private final String unlockedHomeSlotMaterial = Material.SLIME_BALL.name();
    @JsonProperty
    private final List<String> homeShopGuiStructure = List.of(
            "x x x x x x x x x",
            "x . . . . . . . x",
            "x x x x x x x x x"
    );

    public List<HomeSlot> homeSlots() {
        return homeSlots;
    }

    public String lockedHomeSlotMaterial() {
        return lockedHomeSlotMaterial;
    }

    public String unlockedHomeSlotMaterial() {
        return unlockedHomeSlotMaterial;
    }

    public List<String> homeShopGuiStructure() {
        return homeShopGuiStructure;
    }
}
