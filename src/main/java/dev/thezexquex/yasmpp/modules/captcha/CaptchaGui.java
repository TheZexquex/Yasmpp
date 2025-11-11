package dev.thezexquex.yasmpp.modules.captcha;

import de.unknowncity.astralib.paper.api.item.ItemBuilder;
import dev.thezexquex.yasmpp.YasmpPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import xyz.xenondevs.invui.gui.Markers;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.Structure;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.window.Window;

import java.util.function.Consumer;

public class CaptchaGui {
    private Window window;
    private static final Item BORDER_ITEM = Item.simple(ItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).name(Component.empty()).item());

    public void open(Player player, YasmpPlugin plugin, Consumer<CaptchaResult> resultHandler) {
        var captcha = new HaCaptcha(player);
        captcha.generateCaptcha();
        var items = captcha.captchaFields().stream().map(CaptchaItem::new).toList();
        var title = captcha.captchaSet().question();
        var structure = new Structure(
                "x x x x x x x x x",
                "x x . . . . x x x",
                "x x . . . . x x x",
                "x x . . . . x x x",
                "x x . . . . x x x",
                "x x x x x x x B B"
        ).addIngredient('x', BORDER_ITEM)
                .addIngredient('B', Item.builder().setItemProvider(ItemBuilder.of(Material.BLUE_STAINED_GLASS_PANE)
                                .name(Component.text("Bestätigen").color(NamedTextColor.BLUE))
                                .item())
                        .addClickHandler(clickEvent -> {
                            captcha.complete(resultHandler);
                            window.close();
                        })
                        .build())
                .addIngredient('.', Markers.CONTENT_LIST_SLOT_HORIZONTAL);

        var gui = PagedGui.ofItems(structure, items);
        this.window = Window.builder().setUpperGui(gui).setTitle(title).build(player);
        window.addCloseHandler(reason -> {
            if (reason == InventoryCloseEvent.Reason.PLAYER) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> window.open(), 1);
            }
        });
        window.open();
    }
}
