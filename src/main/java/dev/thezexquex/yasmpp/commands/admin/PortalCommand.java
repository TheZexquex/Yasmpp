package dev.thezexquex.yasmpp.commands.admin;

import de.unknowncity.astralib.paper.api.command.PaperCommand;
import dev.thezexquex.yasmpp.YasmpPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;

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
    }

    private void handleSummon(@NonNull CommandContext<Player> context) {
        var player = context.sender();
        var origin = player.getLocation();
        plugin.netherPortalManager().spawnPortalAt(origin);
    }
}
