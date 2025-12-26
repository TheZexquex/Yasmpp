package dev.thezexquex.yasmpp.commands.admin;

import de.unknowncity.astralib.paper.api.command.PaperCommand;
import dev.thezexquex.yasmpp.YasmpPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.spongepowered.configurate.NodePath;

public class PortalCommand extends PaperCommand<YasmpPlugin> {
    public PortalCommand(YasmpPlugin plugin) {
        super(plugin);
    }

    @Override
    public void apply(CommandManager<CommandSender> commandManager) {
        commandManager.command(commandManager.commandBuilder("portal")
                .permission("yasmpp.command.portal")
                .senderType(Player.class)
                .literal("moveHere")
                .handler(this::handleSummon)
        );

        commandManager.command(commandManager.commandBuilder("portal")
                .permission("yasmpp.command.portal")
                .senderType(Player.class)
                .literal("fix")
                .handler(this::handleFix)
        );
    }

    private void handleFix(@NonNull CommandContext<Player> context) {
        var player = context.sender();
        plugin.netherPortalManager().respawnPortalOnServerStart();
        plugin.messenger().sendMessage(player, NodePath.path("command", "portal", "fix"));
    }

    private void handleSummon(@NonNull CommandContext<Player> context) {
        var player = context.sender();
        var origin = player.getLocation();
        plugin.netherPortalManager().spawnPortalAt(origin);
    }
}
