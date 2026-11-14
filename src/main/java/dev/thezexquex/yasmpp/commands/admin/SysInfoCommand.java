package dev.thezexquex.yasmpp.commands.admin;

import de.unknowncity.astralib.paper.api.command.PaperCommand;
import dev.thezexquex.yasmpp.YasmpPlugin;
import dev.thezexquex.yasmpp.modules.sysinfo.SysInfo;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;

import java.util.logging.Level;

public class SysInfoCommand extends PaperCommand<YasmpPlugin> {
    public SysInfoCommand(YasmpPlugin plugin) {
        super(plugin);
    }

    @Override
    public void apply(CommandManager<CommandSender> commandManager) {
        commandManager.command(commandManager.commandBuilder("sysinfo")
                .permission("yasmpp.command.sysinfo")
                .senderType(Player.class)
                .handler(commandContext -> SysInfo.sendSystemInfo(commandContext.sender(), plugin.messenger())
                        .exceptionally(throwable -> {
                            plugin.getLogger().log(Level.SEVERE, "Failed to gather system info", throwable);
                            return null;
                        })));
    }
}
