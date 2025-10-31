package dev.thezexquex.yasmpp.configuration;

import de.unknowncity.astralib.common.configuration.YamlAstraConfiguration;
import de.unknowncity.astralib.common.configuration.annotation.Config;
import de.unknowncity.astralib.common.configuration.setting.defaults.ModernDataBaseSetting;
import de.unknowncity.astralib.libs.com.fasterxml.jackson.annotation.JsonProperty;
import dev.thezexquex.yasmpp.configuration.settings.*;

@Config(targetFile = "plugins/Yasmpp/config.yml")
public class YasmppConfiguration extends YamlAstraConfiguration {

    @JsonProperty
    private ModernDataBaseSetting database = new ModernDataBaseSetting();

    @JsonProperty
    private GeneralSettings general = new GeneralSettings();

    @JsonProperty
    private TeleportSettings teleport = new TeleportSettings();

    @JsonProperty
    private final ChatSettings chat = new ChatSettings();

    @JsonProperty
    private HomeSettings homes = new HomeSettings();

    public YasmppConfiguration() {

    }
    public GeneralSettings general() {
        return general;
    }

    public TeleportSettings teleport() {
        return teleport;
    }

    public ChatSettings chat() {
        return chat;
    }


    public ModernDataBaseSetting database() {
        return database;
    }

    public HomeSettings homes() {
        return homes;
    }
}