package dev.thezexquex.yasmpp.modules.captcha;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.Click;
import xyz.xenondevs.invui.item.AbstractPagedGuiBoundItem;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.ItemWrapper;

public class CaptchaItem extends AbstractPagedGuiBoundItem {
    private final CaptchaField captchaField;
    private ItemStack itemStack;

    public CaptchaItem(CaptchaField captchaField) {
        this.captchaField = captchaField;
        this.itemStack = captchaField.itemStack();
    }

    @Override
    public @NotNull ItemProvider getItemProvider(@NotNull Player viewer) {
        return new ItemWrapper(itemStack);
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull Click click) {
        captchaField.filled(!captchaField.isFilled());
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setEnchantmentGlintOverride(captchaField.isFilled());
        itemStack.setItemMeta(itemMeta);
        player.playSound(player, "minecraft:ui.button.click", 1, 1);
        notifyWindows();
    }
}
