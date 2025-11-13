package dev.thezexquex.yasmpp.modules.captcha;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class CaptchaSet {
    private final Component question;
    private final Set<CaptchaField> captchaFields;

    public CaptchaSet(Component question, Set<CaptchaField> captchaFields) {
        this.question = question;
        this.captchaFields = captchaFields;
    }

    public static CaptchaSet netherBlocks() {
        return new CaptchaSet(
                Component.text("Wähle alle Nether Blöcke"),
                Set.of(
                        new CaptchaField(ItemStack.of(Material.WARPED_STEM), true),
                        new CaptchaField(ItemStack.of(Material.CRIMSON_STEM), true),
                        new CaptchaField(ItemStack.of(Material.NETHERRACK), true),
                        new CaptchaField(ItemStack.of(Material.NETHER_WART_BLOCK), true),

                        new CaptchaField(ItemStack.of(Material.SOUL_SAND), true),
                        new CaptchaField(ItemStack.of(Material.SOUL_SOIL), true),
                        new CaptchaField(ItemStack.of(Material.NETHER_BRICKS), true),
                        new CaptchaField(ItemStack.of(Material.ANCIENT_DEBRIS), true),


                        new CaptchaField(ItemStack.of(Material.WATER_BUCKET), false),
                        new CaptchaField(ItemStack.of(Material.REDSTONE_BLOCK), false),
                        new CaptchaField(ItemStack.of(Material.DIAMOND_ORE), false),
                        new CaptchaField(ItemStack.of(Material.GRASS_BLOCK), false),

                        new CaptchaField(ItemStack.of(Material.HAY_BLOCK), false),
                        new CaptchaField(ItemStack.of(Material.OAK_LOG), false),
                        new CaptchaField(ItemStack.of(Material.SAND), false)
                )
        );
    }

    public static CaptchaSet glass() {
        var possible = List.of(
                Material.GREEN_STAINED_GLASS_PANE,
                Material.BLUE_STAINED_GLASS_PANE,
                Material.YELLOW_STAINED_GLASS_PANE,
                Material.RED_STAINED_GLASS_PANE,
                Material.PURPLE_STAINED_GLASS_PANE
        );

        var searchingFor = possible.get(ThreadLocalRandom.current().nextInt(possible.size()));
        var wrongAnswers = possible.stream().filter(material -> material != searchingFor).toList();
        var answers = new HashSet<CaptchaField>();

        var amount = 30;
        for (int i = 0; i < amount; i++) {
            if (i < amount / 3) {
                answers.add(new CaptchaField(ItemStack.of(searchingFor), true));
            } else {
                answers.add(new CaptchaField(ItemStack.of(wrongAnswers.get(ThreadLocalRandom.current().nextInt(wrongAnswers.size()))), false));
            }
        }

        var textColor = NamedTextColor.NAMES.values().stream().toList().get(ThreadLocalRandom.current().nextInt(NamedTextColor.NAMES.values().size()));

        return new CaptchaSet(
                Component.text("Wähle alle ").append(Component.text(searchingFor.name().split("_")[0]).color(textColor)).append(Component.text(" Glasscheiben")),
                answers
        );
    }

    public static CaptchaSet random() {
        var possible = List.of(
                netherBlocks(),
                glass()
        );
        return possible.get(ThreadLocalRandom.current().nextInt(possible.size()));
    }

    public Component question() {
        return question;
    }

    public Set<CaptchaField> captchaFields() {
        return captchaFields;
    }
}
