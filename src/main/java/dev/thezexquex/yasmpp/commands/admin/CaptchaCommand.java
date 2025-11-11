package dev.thezexquex.yasmpp.commands.admin;

import de.unknowncity.astralib.paper.api.command.PaperCommand;
import dev.thezexquex.yasmpp.YasmpPlugin;
import dev.thezexquex.yasmpp.modules.captcha.CaptchaGui;
import dev.thezexquex.yasmpp.modules.captcha.CaptchaResult;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;

import static org.incendo.cloud.bukkit.parser.PlayerParser.playerParser;

public class CaptchaCommand extends PaperCommand<YasmpPlugin> {
    public CaptchaCommand(YasmpPlugin plugin) {
        super(plugin);
    }

    @Override
    public void apply(CommandManager<CommandSender> commandManager) {
        commandManager.command(commandManager.commandBuilder("captcha")
                .permission("yasmpp.command.captcha")
                .required("target", playerParser())
                .handler(this::handle)
        );
    }

    private void handle(@NonNull CommandContext<CommandSender> context) {
        var target = context.<Player>get("target");
        var sender = context.sender();

        new CaptchaGui().open(target, plugin, (captchaResult) -> {
            if (captchaResult == CaptchaResult.SUCCESS) {
                target.sendMessage("Du bist ein Mensch");
            } else if (captchaResult == CaptchaResult.FAILURE) {
                target.sendMessage("Du bist ein Roboter");
            }
        });
    }
}
