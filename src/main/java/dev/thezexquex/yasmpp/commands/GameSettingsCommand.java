package dev.thezexquex.yasmpp.commands;

import de.unknowncity.astralib.paper.api.command.PaperCommand;
import dev.thezexquex.yasmpp.YasmpPlugin;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.spongepowered.configurate.NodePath;

import static org.incendo.cloud.parser.standard.BooleanParser.booleanParser;
import static org.incendo.cloud.parser.standard.EnumParser.enumParser;
import static org.incendo.cloud.parser.standard.IntegerParser.integerParser;

public class GameSettingsCommand extends PaperCommand<YasmpPlugin> {
    public GameSettingsCommand(YasmpPlugin plugin) {
        super(plugin);
    }

    private enum DamageType {
        TNT, CREEPER
    }

    @Override
    public void apply(CommandManager<CommandSender> commandManager) {
        commandManager.command(commandManager.commandBuilder("gamesettings")
                .literal("lockend")
                .required("lock-end", booleanParser())
                .permission("yasmpp.command.gamesettings")
                .handler(this::handleLockEnd)
        );


        commandManager.command(commandManager.commandBuilder("gamesettings")
                .literal("blockdamage")
                .required("type", enumParser(DamageType.class))
                .required("do-damage", booleanParser())
                .permission("yasmpp.command.gamesettings")
                .handler(this::handleDamage)
        );

        commandManager.command(commandManager.commandBuilder("gamesettings")
                .literal("elytra")
                .literal("maxBoosts")
                .required("max-boosts", integerParser())
                .permission("yasmpp.command.gamesettings")
                .handler(this::handleElytraMaxBoosts)
        );

        commandManager.command(commandManager.commandBuilder("gamesettings")
                .literal("elytra")
                .literal("radius")
                .required("radius", integerParser())
                .permission("yasmpp.command.gamesettings")
                .handler(this::handleElytraRadius)
        );


        commandManager.command(commandManager.commandBuilder("gamesettings")
                .literal("teleport")
                .literal("cancelOnMove")
                .required("cancel", booleanParser())
                .permission("yasmpp.command.gamesettings")
                .handler(this::handleTeleportCancel)
        );

        commandManager.command(commandManager.commandBuilder("gamesettings")
                .literal("teleport")
                .literal("cooldown")
                .required("cooldown", integerParser())
                .permission("yasmpp.command.gamesettings")
                .handler(this::handleTeleportCooldown)
        );

        commandManager.command(commandManager.commandBuilder("gamesettings")
                .literal("teleport")
                .literal("permissionBypassesCooldown")
                .required("bypasses", booleanParser())
                .permission("yasmpp.command.gamesettings")
                .handler(this::handleTeleportPermissionBypass)
        );
    }

    private void handleTeleportCooldown(CommandContext<CommandSender> commandSenderCommandContext) {
        var sender = commandSenderCommandContext.sender();

        var value = (int) commandSenderCommandContext.get("cooldown");

        plugin.configuration().generalSettings().generalSpawnElytraSettings().setMaxBoosts(value);
        plugin.configuration().save();

        plugin.messenger().sendMessage(
                sender,
                NodePath.path("command", "settings", "teleport", "cooldown"),
                TagResolver.resolver("cooldown", Tag.preProcessParsed(String.valueOf(value)))
        );
    }

    private void handleTeleportPermissionBypass(CommandContext<CommandSender> commandSenderCommandContext) {
        var sender = commandSenderCommandContext.sender();

        var value = (boolean) commandSenderCommandContext.get("bypasses");

        if (plugin.configuration().teleportSettings().permissionBypassesCoolDown() == value) {
            plugin.messenger().sendMessage(
                    sender,
                    NodePath.path("command", "settings", "teleport", "permission-bypass", "already", value ? "enabled" : "disabled")
            );
            return;
        }

        plugin.configuration().teleportSettings().setPermissionBypassesCoolDown(value);
        plugin.configuration().save();


        plugin.messenger().sendMessage(
                sender,
                NodePath.path("command", "settings", "teleport", "permission-bypass", "success", value ? "enabled" : "disabled")
        );
    }

    private void handleTeleportCancel(CommandContext<CommandSender> commandSenderCommandContext) {
        var sender = commandSenderCommandContext.sender();

        var value = (boolean) commandSenderCommandContext.get("cancel");

        if (plugin.configuration().teleportSettings().cancelOnMove() == value) {
            plugin.messenger().sendMessage(
                    sender,
                    NodePath.path("command", "settings", "teleport", "cancel-on-move", "already", value ? "enabled" : "disabled")
            );
            return;
        }

        plugin.configuration().teleportSettings().setCancelOnMove(value);
        plugin.configuration().save();

        plugin.messenger().sendMessage(
                sender,
                NodePath.path("command", "settings", "teleport", "cancel-on-move", "success", value ? "enabled" : "disabled")
        );
    }

    private void handleElytraMaxBoosts(CommandContext<CommandSender> commandSenderCommandContext) {
        var sender = commandSenderCommandContext.sender();

        var value = (int) commandSenderCommandContext.get("max-boosts");

        plugin.configuration().generalSettings().generalSpawnElytraSettings().setMaxBoosts(value);
        plugin.configuration().save();

        plugin.messenger().sendMessage(
                sender,
                NodePath.path("command", "settings", "elytra", "max-boosts"),
                TagResolver.resolver("boosts", Tag.preProcessParsed(String.valueOf(value)))
        );
    }

    private void handleElytraRadius(CommandContext<CommandSender> commandSenderCommandContext) {
        var sender = commandSenderCommandContext.sender();

        var value = (int) commandSenderCommandContext.get("radius");

        plugin.configuration().generalSettings().generalSpawnElytraSettings().setRadius(value);
        plugin.configuration().save();

        plugin.messenger().sendMessage(
                sender,
                NodePath.path("command", "settings", "elytra", "radius"),
                TagResolver.resolver("radius", Tag.preProcessParsed(String.valueOf(value)))
        );
    }

    private void handleLockEnd(CommandContext<CommandSender> commandSenderCommandContext) {
        var sender = commandSenderCommandContext.sender();

        var value = (boolean) commandSenderCommandContext.get("lock-end");

        if (plugin.configuration().generalSettings().generalEndSettings().lockEnd() == value) {
            plugin.messenger().sendMessage(
                    sender,
                    NodePath.path("command", "settings", "lockend", "already", value ? "enabled" : "disabled")
            );
            return;
        }

        plugin.configuration().generalSettings().generalEndSettings().setLockEnd(value);
        plugin.configuration().save();

        plugin.messenger().sendMessage(
                sender,
                NodePath.path("command", "settings", "lockend", "success", value ? "enabled" : "disabled")
        );
    }

    private void handleDamage(CommandContext<CommandSender> commandSenderCommandContext) {
        var sender = commandSenderCommandContext.sender();

        var value = (boolean) commandSenderCommandContext.get("do-damage");
        var type = (DamageType) commandSenderCommandContext.get("type");

        if (type == DamageType.TNT) {
            if (plugin.configuration().generalSettings().generalExplosionDamageSettings().doTntDamage() == value) {
                plugin.messenger().sendMessage(
                        sender,
                        NodePath.path("command", "settings", "blockdamage", "already", value ? "enabled" : "disabled"),
                        TagResolver.resolver("type", Tag.preProcessParsed("Tnt"))
                );
                return;
            }

            plugin.configuration().generalSettings().generalExplosionDamageSettings().setDoTntDamage(value);
            plugin.configuration().save();

            plugin.messenger().sendMessage(
                    sender,
                    NodePath.path("command", "settings", "blockdamage", "success", value ? "enabled" : "disabled"),
                    TagResolver.resolver("type", Tag.preProcessParsed("Tnt"))
            );
        } else if (type == DamageType.CREEPER) {
            if (plugin.configuration().generalSettings().generalExplosionDamageSettings().doCreeperDamage() == value) {
                plugin.messenger().sendMessage(
                        sender,
                        NodePath.path("command", "settings", "blockdamage", "already", value ? "enabled" : "disabled"),
                        TagResolver.resolver("type", Tag.preProcessParsed("Creeper"))
                );
                return;
            }

            plugin.configuration().generalSettings().generalExplosionDamageSettings().setDoCreeperDamage(value);
            plugin.configuration().save();

            plugin.messenger().sendMessage(
                    sender,
                    NodePath.path("command", "settings", "blockdamage", "success", value ? "enabled" : "disabled"),
                    TagResolver.resolver("type", Tag.preProcessParsed("Creeper"))
            );
        }
    }
}
