package dev.thezexquex.yasmpp.configuration;

import de.unknowncity.astralib.common.configuration.YamlAstraConfiguration;
import de.unknowncity.astralib.common.configuration.annotation.Config;
import de.unknowncity.astralib.common.configuration.setting.defaults.ModernDataBaseSetting;
import de.unknowncity.astralib.libs.com.fasterxml.jackson.annotation.JsonProperty;
import dev.thezexquex.yasmpp.configuration.settings.ChatSettings;
import dev.thezexquex.yasmpp.configuration.settings.CountDownSettings;
import dev.thezexquex.yasmpp.configuration.settings.GeneralSettings;
import dev.thezexquex.yasmpp.configuration.settings.TeleportSettings;

@Config(targetFile = "plugins/Yasmpp/config.yml")
public class YasmppConfiguration extends YamlAstraConfiguration {

    @JsonProperty
    private ModernDataBaseSetting modernDataBaseSetting = new ModernDataBaseSetting();

    @JsonProperty
    private GeneralSettings generalSettings = new GeneralSettings();

    @JsonProperty
    private TeleportSettings teleportSettings = new TeleportSettings();

    @JsonProperty
    private final ChatSettings chatSettings = new ChatSettings();

    @JsonProperty
    private CountDownSettings countDownSettings = new CountDownSettings();

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