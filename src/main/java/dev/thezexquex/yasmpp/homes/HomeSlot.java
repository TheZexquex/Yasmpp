package dev.thezexquex.yasmpp.homes;

import org.bukkit.Material;

public record HomeSlot(
        int index,
        String currencyName,
        int amount
) {
    public Material currency() {
        return Material.valueOf(currencyName);
    }
}
