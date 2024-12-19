package dev.thezexquex.yasmpp.commands;

import de.unknowncity.astralib.paper.api.command.PaperCommand;
import dev.thezexquex.yasmpp.YasmpPlugin;
import dev.thezexquex.yasmpp.data.database.future.BukkitFutureResult;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.spongepowered.configurate.NodePath;

public class SetSpawnCommand extends PaperCommand<YasmpPlugin> {
    public SetSpawnCommand(YasmpPlugin plugin) {
        super(plugin);
    }

    @Override
    public void apply(CommandManager<CommandSender> commandManager) {
        commandManager.command(commandManager.commandBuilder("setspawn")
                .permission("yasmpp.command.setspawn")
                .senderType(Player.class)
                .handler(this::handleSetSpawn)
        );
    }

    private void handleSetSpawn(CommandContext<Player> commandSenderCommandContext) {
        var player = commandSenderCommandContext.sender();
        var spawnLocation = player.getLocation();

        var spawnLocationString = "world: " + spawnLocation.getWorld().getName() +
                " x: " + (int) spawnLocation.getX() +
                " y: " + (int) spawnLocation.getY() +
                " z: " + (int) spawnLocation.getZ() +
                " yaw: " + (int) spawnLocation.getYaw() +
                " pitch: " + (int) spawnLocation.getPitch();

        var locationService = plugin.locationService();

        BukkitFutureResult.of(locationService.saveLocation("spawn", spawnLocation)).whenComplete(plugin,
                result -> plugin.messenger().sendMessage(
                        player,
                        NodePath.path("command", "setspawn", result ? "success" : "error"),
                        TagResolver.resolver("location", Tag.preProcessParsed(spawnLocationString))
                )
        );
    }
}
