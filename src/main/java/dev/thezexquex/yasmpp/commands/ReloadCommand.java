package dev.thezexquex.yasmpp.commands;

import cloud.commandframework.CommandManager;
import cloud.commandframework.context.CommandContext;
import dev.thezexquex.yasmpp.YasmpPlugin;
import dev.thezexquex.yasmpp.core.command.BaseCommand;
import org.bukkit.command.CommandSender;
import org.spongepowered.configurate.NodePath;

public class ReloadCommand extends BaseCommand {
    public ReloadCommand(YasmpPlugin plugin) {
        super(plugin);
    }

    @Override
    public void register(CommandManager<CommandSender> commandManager) {
        commandManager.command(
                commandManager.commandBuilder("yasmpp")
                        .literal("reload")
                        .permission("yasmpp.command.reload")
                        .handler(this::reload)
        );
    }

    private void reload(CommandContext<CommandSender> commandSenderCommandContext) {
        var commandSender = commandSenderCommandContext.getSender();

        plugin.reloadPlugin();
        plugin.messenger().sendMessage(
                commandSender,
                NodePath.path("command", "reload", "success")
        );
    }
}
