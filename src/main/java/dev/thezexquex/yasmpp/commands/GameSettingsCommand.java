package dev.thezexquex.yasmpp.commands;

import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.BooleanArgument;
import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.context.CommandContext;
import dev.thezexquex.yasmpp.YasmpPlugin;
import dev.thezexquex.yasmpp.core.command.BaseCommand;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.spongepowered.configurate.NodePath;

public class GameSettingsCommand extends BaseCommand {
    public GameSettingsCommand(YasmpPlugin plugin) {
        super(plugin);
    }

    @Override
    public void register(CommandManager<CommandSender> commandManager) {
        commandManager.command(
                commandManager.commandBuilder("gamesettings")
                        .literal("lockend")
                        .argument(BooleanArgument.of("lockEnd"))
                        .permission("yasmpp.command.gamesettings")
                        .handler(this::handleLockEnd)
        );

        commandManager.command(
                commandManager.commandBuilder("gamesettings")
                        .literal("blockdamage")
                        .literal("creeper")
                        .argument(BooleanArgument.of("doDamage"))
                        .permission("yasmpp.command.gamesettings")
                        .handler(this::handleCreeperDamage)
        );

        commandManager.command(
                commandManager.commandBuilder("gamesettings")
                        .literal("blockdamage")
                        .literal("tnt")
                        .argument(BooleanArgument.of("doDamage"))
                        .permission("yasmpp.command.gamesettings")
                        .handler(this::handleTntDamage)
        );

        commandManager.command(
                commandManager.commandBuilder("gamesettings")
                        .literal("elytra")
                        .literal("maxBoosts")
                        .argument(IntegerArgument.of("maxBoosts"))
                        .permission("yasmpp.command.gamesettings")
                        .handler(this::handleElytraMaxBoosts)
        );

        commandManager.command(
                commandManager.commandBuilder("gamesettings")
                        .literal("elytra")
                        .literal("radius")
                        .argument(IntegerArgument.of("radius"))
                        .permission("yasmpp.command.gamesettings")
                        .handler(this::handleElytraRadius)
        );
        

        commandManager.command(
                commandManager.commandBuilder("gamesettings")
                        .literal("teleport")
                        .literal("cancelOnMove")
                        .argument(BooleanArgument.of("cancel"))
                        .permission("yasmpp.command.gamesettings")
                        .handler(this::handleTeleportCancel)
        );

        commandManager.command(
                commandManager.commandBuilder("gamesettings")
                        .literal("teleport")
                        .literal("cooldown")
                        .argument(IntegerArgument.of("cooldown"))
                        .permission("yasmpp.command.gamesettings")
                        .handler(this::handleTeleportCooldown)
        );

        commandManager.command(
                commandManager.commandBuilder("gamesettings")
                        .literal("teleport")
                        .literal("permissionBypassesCooldown")
                        .argument(IntegerArgument.of("bypasses"))
                        .permission("yasmpp.command.gamesettings")
                        .handler(this::handleTeleportPermissionBypass)
        );
    }

    private void handleTeleportCooldown(CommandContext<CommandSender> commandSenderCommandContext) {
        var sender = commandSenderCommandContext.getSender();

        var value = (int) commandSenderCommandContext.get("cooldown");

        plugin.configuration().generalSettings().generalSpawnElytraSettings().setMaxBoosts(value);
        plugin.configurationLoader().saveConfiguration();

        plugin.messenger().sendMessage(
                sender,
                NodePath.path("command", "settings", "teleport", "cooldown"),
                TagResolver.resolver("cooldown", Tag.preProcessParsed(String.valueOf(value)))
        );
    }

    private void handleTeleportPermissionBypass(CommandContext<CommandSender> commandSenderCommandContext) {
        var sender = commandSenderCommandContext.getSender();

        var value = (boolean) commandSenderCommandContext.get("bypasses");

        if (plugin.configuration().teleportSettings().permissionBypassesCoolDown() == value) {
            plugin.messenger().sendMessage(
                    sender,
                    NodePath.path("command", "settings", "teleport", "permission-bypass", "already", value ? "enabled" : "disabled")
            );
            return;
        }

        plugin.configuration().teleportSettings().setPermissionBypassesCoolDown(value);
        plugin.configurationLoader().saveConfiguration();

        plugin.messenger().sendMessage(
                sender,
                NodePath.path("command", "settings", "teleport", "permission-bypass", "success", value ? "enabled" : "disabled")
        );
    }

    private void handleTeleportCancel(CommandContext<CommandSender> commandSenderCommandContext) {
        var sender = commandSenderCommandContext.getSender();

        var value = (boolean) commandSenderCommandContext.get("cancel");

        if (plugin.configuration().teleportSettings().cancelOnMove() == value) {
            plugin.messenger().sendMessage(
                    sender,
                    NodePath.path("command", "settings", "teleport", "cancel-on-move", "already", value ? "enabled" : "disabled")
            );
            return;
        }

        plugin.configuration().teleportSettings().setCancelOnMove(value);
        plugin.configurationLoader().saveConfiguration();

        plugin.messenger().sendMessage(
                sender,
                NodePath.path("command", "settings", "teleport", "cancel-on-move", "success", value ? "enabled" : "disabled")
        );
    }

    private void handleElytraMaxBoosts(CommandContext<CommandSender> commandSenderCommandContext) {
        var sender = commandSenderCommandContext.getSender();

        var value = (int) commandSenderCommandContext.get("maxBoosts");

        plugin.configuration().generalSettings().generalSpawnElytraSettings().setMaxBoosts(value);
        plugin.configurationLoader().saveConfiguration();

        plugin.messenger().sendMessage(
                sender,
                NodePath.path("command", "settings", "elytra", "max-boosts"),
                TagResolver.resolver("boosts", Tag.preProcessParsed(String.valueOf(value)))
        );
    }

    private void handleElytraRadius(CommandContext<CommandSender> commandSenderCommandContext) {
        var sender = commandSenderCommandContext.getSender();

        var value = (int) commandSenderCommandContext.get("radius");

        plugin.configuration().generalSettings().generalSpawnElytraSettings().setRadius(value);
        plugin.configurationLoader().saveConfiguration();

        plugin.messenger().sendMessage(
                sender,
                NodePath.path("command", "settings", "elytra", "radius"),
                TagResolver.resolver("radius", Tag.preProcessParsed(String.valueOf(value)))
        );
    }

    private void handleLockEnd(CommandContext<CommandSender> commandSenderCommandContext) {
        var sender = commandSenderCommandContext.getSender();

        var value = (boolean) commandSenderCommandContext.get("lockEnd");

        if (plugin.configuration().generalSettings().generalEndSettings().lockEnd() == value) {
            plugin.messenger().sendMessage(
                    sender,
                    NodePath.path("command", "settings", "lockend", "already", value ? "enabled" : "disabled")
            );
            return;
        }

        plugin.configuration().generalSettings().generalEndSettings().setLockEnd(value);
        plugin.configurationLoader().saveConfiguration();

        plugin.messenger().sendMessage(
                sender,
                NodePath.path("command", "settings", "lockend", "success", value ? "enabled" : "disabled")
        );
    }

    private void handleCreeperDamage(CommandContext<CommandSender> commandSenderCommandContext) {
        var sender = commandSenderCommandContext.getSender();

        var value = (boolean) commandSenderCommandContext.get("doDamage");

        if (plugin.configuration().generalSettings().generalExplosionDamageSettings().doCreeperDamage() == value) {
            plugin.messenger().sendMessage(
                    sender,
                    NodePath.path("command", "settings", "blockdamage", "already", value ? "enabled" : "disabled"),
                    TagResolver.resolver("type", Tag.preProcessParsed("Creeper"))
            );
            return;
        }

        plugin.configuration().generalSettings().generalExplosionDamageSettings().setDoCreeperDamage(value);
        plugin.configurationLoader().saveConfiguration();

        plugin.messenger().sendMessage(
                sender,
                NodePath.path("command", "settings", "blockdamage", "success", value ? "enabled" : "disabled"),
                TagResolver.resolver("type", Tag.preProcessParsed("Creeper"))
        );
    }

    private void handleTntDamage(CommandContext<CommandSender> commandSenderCommandContext) {
        var sender = commandSenderCommandContext.getSender();

        var value = (boolean) commandSenderCommandContext.get("doDamage");

        if (plugin.configuration().generalSettings().generalExplosionDamageSettings().doTntDamage() == value) {
            plugin.messenger().sendMessage(
                    sender,
                    NodePath.path("command", "settings", "blockdamage", "already", value ? "enabled" : "disabled"),
                    TagResolver.resolver("type", Tag.preProcessParsed("Tnt"))
            );
            return;
        }

        plugin.configuration().generalSettings().generalExplosionDamageSettings().setDoTntDamage(value);
        plugin.configurationLoader().saveConfiguration();

        plugin.messenger().sendMessage(
                sender,
                NodePath.path("command", "settings", "blockdamage", "success", value ? "enabled" : "disabled"),
                TagResolver.resolver("type", Tag.preProcessParsed("Tnt"))
        );
    }
}
