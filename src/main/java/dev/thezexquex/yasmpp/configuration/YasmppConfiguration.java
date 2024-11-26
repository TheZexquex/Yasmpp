package dev.thezexquex.yasmpp.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.unknowncity.astralib.common.configuration.YamlAstraConfiguration;
import de.unknowncity.astralib.common.configuration.annotation.Config;
import de.unknowncity.astralib.common.configuration.setting.defaults.ModernDataBaseSetting;
import dev.thezexquex.yasmpp.configuration.settings.ChatSettings;
import dev.thezexquex.yasmpp.configuration.settings.CountDownSettings;
import dev.thezexquex.yasmpp.configuration.settings.GeneralSettings;
import dev.thezexquex.yasmpp.configuration.settings.TeleportSettings;

@Config(targetFile = "plugins/Yasmpp/config.yml")
public class YasmppConfiguration extends YamlAstraConfiguration {

    @JsonProperty
    ModernDataBaseSetting modernDataBaseSetting = new ModernDataBaseSetting();

    private final GeneralSettings generalSettings = new GeneralSettings();

    private final TeleportSettings teleportSettings = new TeleportSettings();

    private final ChatSettings chatSettings = new ChatSettings();

    private final CountDownSettings countDownSettings = new CountDownSettings();

    public YasmppConfiguration() {

    }
    public GeneralSettings generalSettings() {
        return generalSettings;
    }

    public TeleportSettings teleportSettings() {
        return teleportSettings;
    }

    public ChatSettings chatSettings() {
        return chatSettings;
    }

    public CountDownSettings countDownSettings() {
        return countDownSettings;
    }

    public ModernDataBaseSetting modernDataBaseSetting() {
        return modernDataBaseSetting;
    }
}