package dev.thezexquex.yasmpp.homes.gui.item;

import de.unknowncity.astralib.paper.api.inventory.InventoryUtil;
import de.unknowncity.astralib.paper.api.item.ItemBuilder;
import dev.thezexquex.yasmpp.YasmpPlugin;
import dev.thezexquex.yasmpp.data.entity.SmpPlayer;
import dev.thezexquex.yasmpp.homes.HomeSlot;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.NodePath;
import xyz.xenondevs.invui.Click;
import xyz.xenondevs.invui.item.AbstractItem;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.ItemWrapper;

import java.util.Arrays;

public class HomeSlotItem extends AbstractItem {
    private final HomeSlot homeSlot;
    private final SmpPlayer smpPlayer;
    private final YasmpPlugin plugin;

    public HomeSlotItem(HomeSlot homeSlot, SmpPlayer smpPlayer, YasmpPlugin plugin) {
        this.homeSlot = homeSlot;
        this.smpPlayer = smpPlayer;
        this.plugin = plugin;
    }

    @Override
    public @NotNull ItemProvider getItemProvider(@NotNull Player player) {
        var lockedItem = ItemBuilder.of(Material.valueOf(plugin.configuration().homeSettings().lockedHomeSlotMaterial()))
                .name(plugin.messenger().component(player, NodePath.path("gui", "homeslotshop", "item", "locked-name")))
                .lore(plugin.messenger().componentList(player, NodePath.path("gui", "homeslotshop", "item", "locked-lore"),
                        Placeholder.unparsed("homeslot", String.valueOf(homeSlot.index())),
                        Placeholder.unparsed("amount", String.valueOf(homeSlot.amount())),
                        Placeholder.component("material", Component.translatable(homeSlot.currency().translationKey()))
                ))
                .item();

        var unlockedItem = ItemBuilder.of(Material.valueOf(plugin.configuration().homeSettings().unlockedHomeSlotMaterial()))
                .name(plugin.messenger().component(player, NodePath.path("gui", "homeslotshop", "item", "unlocked-name")))
                .lore(plugin.messenger().componentList(player, NodePath.path("gui", "homeslotshop", "item", "unlocked-lore"),
                        Placeholder.unparsed("homeslot", String.valueOf(homeSlot.index())),
                        Placeholder.unparsed("amount", String.valueOf(homeSlot.amount())),
                        Placeholder.component("material", Component.translatable(homeSlot.currency().translationKey()))
                ))
                .item();
        return new ItemWrapper(isLocked() ? lockedItem : unlockedItem);
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull Click click) {
        if (!isLocked()) {
            return;
        }

        if (!InventoryUtil.hasEnoughItems(player, ItemStack.of(homeSlot.currency()), homeSlot.amount())) {
            var has = Arrays.stream(player.getInventory().getContents())
                    .filter(itemStack -> itemStack != null && itemStack.getType() == homeSlot.currency())
                    .count();
            plugin.messenger().sendMessage(player, NodePath.path("command", "homeslot", "not-enough-currency"),
                    Placeholder.component("material", Component.translatable(homeSlot.currency().translationKey())),
                    Placeholder.unparsed("has", String.valueOf(has)), Placeholder.unparsed("needs", String.valueOf(homeSlot.amount())));
            player.playSound(player.getLocation(), "minecraft:block.note_block.bass", 1, 0);
            return;
        }

        if (homeSlot.index() > smpPlayer.maxHomeSlots() + 1) {
            plugin.messenger().sendMessage(player, NodePath.path("command", "homeslot", "out-of-order"));
            player.playSound(player.getLocation(), "minecraft:block.note_block.bass", 1, 0);
            return;
        }


        InventoryUtil.removeSpecificItemCount(player, ItemStack.of(homeSlot.currency()), homeSlot.amount());
        plugin.homeService().incrementHomeSlots(player.getUniqueId(), 1);
        player.playSound(player.getLocation(), "minecraft:entity.player.levelup", 1, 1);
        plugin.messenger().sendMessage(player, NodePath.path("command", "homeslot", "success"),
                Placeholder.unparsed("total", String.valueOf(smpPlayer.maxHomeSlots()))
        );

        notifyWindows();
    }

    private boolean isLocked() {
        return smpPlayer.maxHomeSlots() < homeSlot.index();
    }
}
