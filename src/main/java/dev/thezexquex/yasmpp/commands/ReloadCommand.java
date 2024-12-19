package dev.thezexquex.yasmpp.commands;

import de.unknowncity.astralib.paper.api.command.PaperCommand;
import dev.thezexquex.yasmpp.YasmpPlugin;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.spongepowered.configurate.NodePath;

public class ReloadCommand extends PaperCommand<YasmpPlugin> {
    public ReloadCommand(YasmpPlugin plugin) {
        super(plugin);
    }

    @Override
    public void apply(CommandManager<CommandSender> commandManager) {
        commandManager.command(commandManager.commandBuilder("yasmpp")
                .literal("reload")
                .permission("yasmpp.command.reload")
                .handler(this::reload)
        );
    }

    private void reload(CommandContext<CommandSender> commandSenderCommandContext) {
        var commandSender = commandSenderCommandContext.sender();

        plugin.reloadPlugin();
        plugin.messenger().sendMessage(
                commandSender,
                NodePath.path("command", "reload", "success")
        );
    }
}
