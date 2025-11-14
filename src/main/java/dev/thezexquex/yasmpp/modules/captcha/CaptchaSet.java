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

    public static CaptchaSet goodNutrition() {
        return new CaptchaSet(
                Component.text("Wähle Essen mit >= 1.6 Saturation"),
                Set.of(
                        new CaptchaField(ItemStack.of(Material.ENCHANTED_GOLDEN_APPLE), true),
                        new CaptchaField(ItemStack.of(Material.GOLDEN_APPLE), true),
                        new CaptchaField(ItemStack.of(Material.GOLDEN_CARROT), true),
                        new CaptchaField(ItemStack.of(Material.COOKED_MUTTON), true),
                        new CaptchaField(ItemStack.of(Material.COOKED_PORKCHOP), true),
                        new CaptchaField(ItemStack.of(Material.COOKED_BEEF), true),
                        new CaptchaField(ItemStack.of(Material.COOKED_SALMON), true),

                        new CaptchaField(ItemStack.of(Material.CAKE), false),
                        new CaptchaField(ItemStack.of(Material.COOKIE), false),
                        new CaptchaField(ItemStack.of(Material.DRIED_KELP), false),
                        new CaptchaField(ItemStack.of(Material.GLOW_BERRIES), false),
                        new CaptchaField(ItemStack.of(Material.HONEY_BOTTLE), false),
                        new CaptchaField(ItemStack.of(Material.PUFFERFISH), false),
                        new CaptchaField(ItemStack.of(Material.COD), false),
                        new CaptchaField(ItemStack.of(Material.SALMON), false),
                        new CaptchaField(ItemStack.of(Material.ROTTEN_FLESH), false),
                        new CaptchaField(ItemStack.of(Material.SPIDER_EYE), false),
                        new CaptchaField(ItemStack.of(Material.SWEET_BERRIES), false),
                        new CaptchaField(ItemStack.of(Material.TROPICAL_FISH), false),
                        new CaptchaField(ItemStack.of(Material.APPLE), false),
                        new CaptchaField(ItemStack.of(Material.MELON_SLICE), false),
                        new CaptchaField(ItemStack.of(Material.POTATO), false),
                        new CaptchaField(ItemStack.of(Material.BEEF), false),
                        new CaptchaField(ItemStack.of(Material.RABBIT), false),
                        new CaptchaField(ItemStack.of(Material.POISONOUS_POTATO), false)
                )
        );
    }

    public static CaptchaSet badNutrition() {
        return new CaptchaSet(
                Component.text("Wähle Essen mit <= 0.2 Saturation"),
                Set.of(
                        new CaptchaField(ItemStack.of(Material.ENCHANTED_GOLDEN_APPLE), false),
                        new CaptchaField(ItemStack.of(Material.GOLDEN_APPLE), false),
                        new CaptchaField(ItemStack.of(Material.GOLDEN_CARROT), false),
                        new CaptchaField(ItemStack.of(Material.COOKED_MUTTON), false),
                        new CaptchaField(ItemStack.of(Material.COOKED_PORKCHOP), false),
                        new CaptchaField(ItemStack.of(Material.COOKED_BEEF), false),
                        new CaptchaField(ItemStack.of(Material.COOKED_SALMON), false),
                        new CaptchaField(ItemStack.of(Material.BAKED_POTATO), false),
                        new CaptchaField(ItemStack.of(Material.MUSHROOM_STEW), false),
                        new CaptchaField(ItemStack.of(Material.SUSPICIOUS_STEW), false),
                        new CaptchaField(ItemStack.of(Material.MUSHROOM_STEW), false),
                        new CaptchaField(ItemStack.of(Material.COOKED_RABBIT), false),
                        new CaptchaField(ItemStack.of(Material.COOKED_CHICKEN), false),
                        new CaptchaField(ItemStack.of(Material.COOKED_COD), false),
                        new CaptchaField(ItemStack.of(Material.BREAD), false),
                        new CaptchaField(ItemStack.of(Material.CARROT), false),

                        new CaptchaField(ItemStack.of(Material.APPLE), true),
                        new CaptchaField(ItemStack.of(Material.MELON_SLICE), true),
                        new CaptchaField(ItemStack.of(Material.POTATO), true),
                        new CaptchaField(ItemStack.of(Material.BEEF), true),
                        new CaptchaField(ItemStack.of(Material.RABBIT), true),
                        new CaptchaField(ItemStack.of(Material.POISONOUS_POTATO), true)
                )
        );
    }

    public static CaptchaSet mobsInPlains() {
        return new CaptchaSet(
                Component.text("Wähle Mobs die in Plains spawnen"),
                Set.of(
                        new CaptchaField(ItemStack.of(Material.CREEPER_SPAWN_EGG), true),
                        new CaptchaField(ItemStack.of(Material.SKELETON_SPAWN_EGG), true),
                        new CaptchaField(ItemStack.of(Material.SPIDER_SPAWN_EGG), true),
                        new CaptchaField(ItemStack.of(Material.ZOMBIE_SPAWN_EGG), true),
                        new CaptchaField(ItemStack.of(Material.SHEEP_SPAWN_EGG), true),
                        new CaptchaField(ItemStack.of(Material.PIG_SPAWN_EGG), true),
                        new CaptchaField(ItemStack.of(Material.CHICKEN_SPAWN_EGG), true),
                        new CaptchaField(ItemStack.of(Material.COW_SPAWN_EGG), true),
                        new CaptchaField(ItemStack.of(Material.HORSE_SPAWN_EGG), true),
                        new CaptchaField(ItemStack.of(Material.DONKEY_SPAWN_EGG), true),

                        new CaptchaField(ItemStack.of(Material.HUSK_SPAWN_EGG), false),
                        new CaptchaField(ItemStack.of(Material.DROWNED_SPAWN_EGG), false),
                        new CaptchaField(ItemStack.of(Material.CAMEL_SPAWN_EGG), false),
                        new CaptchaField(ItemStack.of(Material.SQUID_SPAWN_EGG), false),
                        new CaptchaField(ItemStack.of(Material.SALMON_SPAWN_EGG), false),
                        new CaptchaField(ItemStack.of(Material.BOGGED_SPAWN_EGG), false),
                        new CaptchaField(ItemStack.of(Material.STRAY_SPAWN_EGG), false),
                        new CaptchaField(ItemStack.of(Material.ELDER_GUARDIAN_SPAWN_EGG), false),
                        new CaptchaField(ItemStack.of(Material.RABBIT_SPAWN_EGG), false),
                        new CaptchaField(ItemStack.of(Material.SNOW_GOLEM_SPAWN_EGG), false),
                        new CaptchaField(ItemStack.of(Material.GOAT_SPAWN_EGG), false),
                        new CaptchaField(ItemStack.of(Material.BLAZE_SPAWN_EGG), false),
                        new CaptchaField(ItemStack.of(Material.WITHER_SKELETON_SPAWN_EGG), false)
                )
        );
    }

    public static CaptchaSet materialsForTools() {
        return new CaptchaSet(
                Component.text("Wähle Items zum Craften v. Tools"),
                Set.of(
                        new CaptchaField(ItemStack.of(Material.DIAMOND), true),
                        new CaptchaField(ItemStack.of(Material.NETHERITE_INGOT), true),
                        new CaptchaField(ItemStack.of(Material.IRON_INGOT), true),
                        new CaptchaField(ItemStack.of(Material.GOLD_INGOT), true),
                        new CaptchaField(ItemStack.of(Material.COPPER_INGOT), true),
                        new CaptchaField(ItemStack.of(Material.OAK_PLANKS), true),
                        new CaptchaField(ItemStack.of(Material.BIRCH_PLANKS), true),
                        new CaptchaField(ItemStack.of(Material.COBBLESTONE), true),
                        new CaptchaField(ItemStack.of(Material.COBBLED_DEEPSLATE), true),
                        new CaptchaField(ItemStack.of(Material.BLACKSTONE), true),
                        new CaptchaField(ItemStack.of(Material.CHERRY_PLANKS), true),
                        new CaptchaField(ItemStack.of(Material.ACACIA_PLANKS), true),
                        new CaptchaField(ItemStack.of(Material.STICK), true),

                        new CaptchaField(ItemStack.of(Material.EMERALD), false),
                        new CaptchaField(ItemStack.of(Material.RAW_GOLD), false),
                        new CaptchaField(ItemStack.of(Material.RAW_IRON), false),
                        new CaptchaField(ItemStack.of(Material.RAW_COPPER), false),
                        new CaptchaField(ItemStack.of(Material.REDSTONE), false),
                        new CaptchaField(ItemStack.of(Material.GILDED_BLACKSTONE), false),
                        new CaptchaField(ItemStack.of(Material.STRIPPED_ACACIA_LOG), false),
                        new CaptchaField(ItemStack.of(Material.STRIPPED_OAK_LOG), false),
                        new CaptchaField(ItemStack.of(Material.STRIPPED_CHERRY_LOG), false),
                        new CaptchaField(ItemStack.of(Material.TUFF), false),
                        new CaptchaField(ItemStack.of(Material.BASALT), false),
                        new CaptchaField(ItemStack.of(Material.BLUE_BED), false),
                        new CaptchaField(ItemStack.of(Material.STONE_PICKAXE), false),
                        new CaptchaField(ItemStack.of(Material.GOLDEN_SHOVEL), false),
                        new CaptchaField(ItemStack.of(Material.IRON_AXE), false),
                        new CaptchaField(ItemStack.of(Material.NETHERITE_HOE), false)
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
                glass(),
                goodNutrition(),
                badNutrition(),
                mobsInPlains(),
                materialsForTools()
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
