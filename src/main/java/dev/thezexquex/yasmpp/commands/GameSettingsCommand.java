package dev.thezexquex.yasmpp.commands;

import de.unknowncity.astralib.paper.api.command.PaperCommand;
import dev.thezexquex.yasmpp.YasmpPlugin;
import dev.thezexquex.yasmpp.gamesettings.BooleanGameSetting;
import dev.thezexquex.yasmpp.gamesettings.EnumGameSetting;
import dev.thezexquex.yasmpp.gamesettings.GameSetting;
import dev.thezexquex.yasmpp.gamesettings.IntegerGameSetting;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.suggestion.Suggestion;
import org.spongepowered.configurate.NodePath;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.incendo.cloud.parser.standard.StringParser.stringParser;

public class GameSettingsCommand extends PaperCommand<YasmpPlugin> {
    public GameSettingsCommand(YasmpPlugin plugin) {
        super(plugin);
    }

    @Override
    public void apply(CommandManager<CommandSender> commandManager) {
        commandManager.command(commandManager.commandBuilder("gamesettings")
                .required("setting", stringParser(), (_, _) -> CompletableFuture.completedFuture(
                        plugin.gameSettingRegistry().all().stream()
                                .map(GameSetting::id)
                                .map(Suggestion::suggestion)
                                .toList()
                ))
                .optional("value", stringParser(), (context, _) -> {
                    String settingName = context.get("setting");
                    var settingOpt = plugin.gameSettingRegistry().get(settingName);
                    if (settingOpt.isPresent() && settingOpt.get() instanceof EnumGameSetting<?> enumSetting) {
                        return CompletableFuture.completedFuture(
                                Arrays.stream(enumSetting.enumClass().getEnumConstants())
                                        .map(Enum::name)
                                        .map(String::toLowerCase)
                                        .map(Suggestion::suggestion)
                                        .toList()
                        );
                    }
                    if (settingOpt.isPresent() && settingOpt.get() instanceof BooleanGameSetting) {
                        return CompletableFuture.completedFuture(
                                List.of(Suggestion.suggestion("true"), Suggestion.suggestion("false"))
                        );
                    }
                    return CompletableFuture.completedFuture(List.of());
                })
                .permission("yasmpp.command.gamesettings")
                .handler(this::handleGameSetting)
        );
    }

    private void handleGameSetting(CommandContext<CommandSender> context) {
        var sender = context.sender();
        String settingName = context.get("setting");
        Optional<String> valueOptional = context.optional("value");

        Optional<GameSetting<?>> settingOpt = plugin.gameSettingRegistry().get(settingName);
        if (settingOpt.isEmpty()) {
            plugin.messenger().sendMessage(sender, NodePath.path("command", "settings", "not-found"), TagResolver.resolver("setting", Tag.preProcessParsed(settingName)));
            return;
        }

        GameSetting<?> setting = settingOpt.get();

        if (valueOptional.isEmpty()) {
            plugin.messenger().sendMessage(sender, NodePath.path("command", "settings", "current-value"),
                    TagResolver.resolver("setting", Tag.preProcessParsed(setting.id())),
                    TagResolver.resolver("value", Tag.preProcessParsed(setting.getValueAsString())));
            return;
        }

        String valueStr = valueOptional.get();

        try {
            switch (setting) {
                case BooleanGameSetting booleanSetting -> {
                    boolean val = Boolean.parseBoolean(valueStr);
                    booleanSetting.setValue(val);
                }
                case IntegerGameSetting integerSetting -> {
                    int val = Integer.parseInt(valueStr);
                    integerSetting.setValue(val);
                }
                case EnumGameSetting<?> enumSetting -> updateEnumSetting(enumSetting, valueStr);
                default -> {
                }
            }
            plugin.configuration().save();
            plugin.messenger().sendMessage(sender, NodePath.path("command", "settings", "success"),
                    TagResolver.resolver("setting", Tag.preProcessParsed(setting.id())),
                    TagResolver.resolver("value", Tag.preProcessParsed(setting.getValueAsString())));
        } catch (Exception e) {
            plugin.messenger().sendMessage(sender, NodePath.path("command", "settings", "invalid-value"),
                    TagResolver.resolver("setting", Tag.preProcessParsed(setting.id())),
                    TagResolver.resolver("value", Tag.preProcessParsed(valueStr)));
        }
    }

    private <E extends Enum<E>> void updateEnumSetting(EnumGameSetting<E> setting, String valueStr) {
        E val = Enum.valueOf(setting.enumClass(), valueStr.toUpperCase());
        setting.setValue(val);
    }
}
