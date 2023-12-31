package dev.thezexquex.yasmpp.modules.stacksize;

import dev.thezexquex.yasmpp.YasmpPlugin;
import net.minecraft.world.item.Item;
import org.bukkit.Material;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.logging.Level;

public class StackSizeChanger {
    private static final int DEFAULT_STACK_SIZE = 64;
    private static final int NEW_STACK_SIZE = 69;
    private final String serverPackageName;
    private final YasmpPlugin plugin;

    public StackSizeChanger(YasmpPlugin plugin) {
        this.plugin = plugin;
        this.serverPackageName = plugin.getServer().getClass().getPackage().getName();
    }

    public void changeAllItemStackSizes() {
        var materials = Arrays.stream(Material.values()).toList();
        materials.forEach(material -> {
            Item item = null;
            try {
                item = (Item) Class.forName("org.bukkit.craftbukkit." + serverPackageName.split("\\.")[3] + ".util.CraftMagicNumbers")
                        .getDeclaredMethod("getItem", Material.class).invoke(null, material);
            } catch (NoSuchMethodException | ClassNotFoundException | InvocationTargetException |
                     IllegalAccessException e) {
                plugin.getLogger().log(Level.SEVERE,"Failed to set stack size for item " + item, e);
            }
            setNewStackSize(material, item);
        });
    }

    private void setNewStackSize(Material material, Item item) {
        try {
            if (material.getMaxStackSize() == DEFAULT_STACK_SIZE) {
                Field materialField = Material.class.getDeclaredField("maxStack");
                materialField.setAccessible(true);
                materialField.setInt(material, StackSizeChanger.NEW_STACK_SIZE);

                Field itemField = Item.class.getDeclaredField("maxStackSize");
                itemField.setAccessible(true);
                itemField.setInt(item, StackSizeChanger.NEW_STACK_SIZE);
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE,"Failed to set stack size for item " + item + " to " + StackSizeChanger.NEW_STACK_SIZE, e);
        }
    }
}
