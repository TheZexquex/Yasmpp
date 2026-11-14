package dev.thezexquex.yasmpp.commands.admin;

import de.unknowncity.astralib.paper.api.command.PaperCommand;
import dev.thezexquex.yasmpp.YasmpPlugin;
import dev.thezexquex.yasmpp.modules.sysinfo.SysInfo;
import dev.thezexquex.yasmpp.modules.sysinfo.SysInfoLiveView;
import dev.thezexquex.yasmpp.modules.sysinfo.SysInfoSection;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.spongepowered.configurate.NodePath;

import java.util.Set;
import java.util.logging.Level;

public class SysInfoCommand extends PaperCommand<YasmpPlugin> {
    private final SysInfo sysInfo;
    private final SysInfoLiveView liveView;

    public SysInfoCommand(YasmpPlugin plugin) {
        super(plugin);
        this.sysInfo = new SysInfo(plugin);
        this.liveView = new SysInfoLiveView(plugin);
    }

    @Override
    public void apply(CommandManager<CommandSender> commandManager) {
        var base = commandManager.commandBuilder("sysinfo")
                .permission("yasmpp.command.sysinfo");

        commandManager.command(withViewFlags(commandManager, base)
                .handler(context -> send(context, SysInfoSection.overview(context.flags().isPresent("detailed")))));

        for (var section : SysInfoSection.values()) {
            commandManager.command(withViewFlags(commandManager, base.literal(section.commandName()))
                    .handler(context -> send(context, Set.of(section))));
        }

        commandManager.command(base.literal("live")
                .senderType(Player.class)
                .handler(context -> {
                    var enabled = liveView.toggle(context.sender());
                    plugin.messenger().sendMessage(context.sender(),
                            NodePath.path("command", "sysinfo", "live", enabled ? "enabled" : "disabled"));
                }));
    }

    private Command.Builder<CommandSender> withViewFlags(CommandManager<CommandSender> commandManager, Command.Builder<CommandSender> builder) {
        return builder
                .flag(commandManager.flagBuilder("bars").withAliases("b"))
                .flag(commandManager.flagBuilder("detailed").withAliases("d"));
    }

    private void send(CommandContext<CommandSender> context, Set<SysInfoSection> sections) {
        var flags = context.flags();
        sysInfo.sendSystemInfo(context.sender(), sections, flags.isPresent("bars"), flags.isPresent("detailed"))
                .exceptionally(throwable -> {
                    plugin.getLogger().log(Level.SEVERE, "Failed to gather system info", throwable);
                    return null;
                });
    }
}
