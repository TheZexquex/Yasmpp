package dev.thezexquex.yasmpp.homes.gui;

import de.unknowncity.astralib.paper.api.item.ItemBuilder;
import dev.thezexquex.yasmpp.YasmpPlugin;
import dev.thezexquex.yasmpp.homes.gui.item.HomeSlotItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.NodePath;
import xyz.xenondevs.invui.gui.Markers;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.Structure;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemWrapper;
import xyz.xenondevs.invui.window.Window;

public class HomeSlotShop {

    public static void open(Player player, YasmpPlugin plugin) {
        var borderItem = ItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).name(Component.empty()).item();
        var backgroundItem = ItemBuilder.of(Material.GRAY_STAINED_GLASS_PANE).name(Component.empty()).item();

        var title = plugin.messenger().component(player, NodePath.path("gui", "homeslotshop", "title"));

        var homeSlots = plugin.configuration().homes().homeSlots();

        var smpPlayer = plugin.smpPlayerService().getSmpPlayer(player);
        if (smpPlayer.isEmpty()) {
            plugin.messenger().sendMessage(player, NodePath.path("command", "homeslot", "error"));
            player.playSound(player.getLocation(), "minecraft:block.note_block.bass", 1, 0);
            return;
        }

        var slotItems = homeSlots.stream()
                .map(homeSlot -> new HomeSlotItem(homeSlot, smpPlayer.get(), plugin))
                .toList();

        var structure = new Structure(
                plugin.configuration().homes().homeShopGuiStructure().toArray(String[]::new)
        )
                .addIngredient('x', Item.simple(borderItem))
                .addIngredient('.', Markers.CONTENT_LIST_SLOT_HORIZONTAL);

        var gui = PagedGui.ofItems(structure, slotItems);
        gui.setBackground(new ItemWrapper(backgroundItem));

        Window.builder()
                .setUpperGui(gui)
                .setTitle(title)
                .build(player)
                .open();
    }
}
