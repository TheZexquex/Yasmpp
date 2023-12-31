package dev.thezexquex.yasmpp.commands;

import cloud.commandframework.CommandManager;
import cloud.commandframework.context.CommandContext;
import dev.thezexquex.yasmpp.YasmpPlugin;
import dev.thezexquex.yasmpp.core.command.BaseCommand;
import dev.thezexquex.yasmpp.core.data.database.furure.BukkitFutureResult;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.NodePath;

public class SetSpawnCommand extends BaseCommand {
    public SetSpawnCommand(YasmpPlugin plugin) {
        super(plugin);
    }

    @Override
    public void register(CommandManager<CommandSender> commandManager) {
        commandManager.command(
                commandManager.commandBuilder("setspawn")
                        .permission("yasmpp.command.setspawn")
                        .senderType(Player.class)
                        .handler(this::handleSetSpawn)
        );
    }

    private void handleSetSpawn(CommandContext<CommandSender> commandSenderCommandContext) {
        var player = (Player) commandSenderCommandContext.getSender();
        var spawnLocation = player.getLocation();

        var spawnLocationString = "world: " + spawnLocation.getWorld().getName() +
                " x: " + (int) spawnLocation.getX() +
                " y: " + (int) spawnLocation.getY() +
                " z: " + (int) spawnLocation.getZ() +
                " yaw: " + (int) spawnLocation.getYaw() +
                " pitch: " + (int) spawnLocation.getPitch();

        var locationService = plugin.locationService();
        if (locationService.existsCachedLocation("spawn")) {
            BukkitFutureResult.of(locationService.updateLocation("spawn", spawnLocation)).whenComplete(plugin,
                    result -> plugin.messenger().sendMessage(
                            player,
                            NodePath.path("command", "setspawn", result ? "success" : "error"),
                            TagResolver.resolver("location", Tag.preProcessParsed(spawnLocationString))
                    )
            );
            return;
        }

        BukkitFutureResult.of(locationService.saveLocation("spawn", spawnLocation)).whenComplete(plugin,
                result -> plugin.messenger().sendMessage(
                        player,
                        NodePath.path("command", "setspawn", result ? "success" : "error"),
                        TagResolver.resolver("location", Tag.preProcessParsed(spawnLocationString))
                )
        );
    }
}
