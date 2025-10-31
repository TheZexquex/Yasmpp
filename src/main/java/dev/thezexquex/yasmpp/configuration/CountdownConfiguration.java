package dev.thezexquex.yasmpp.configuration;

import de.unknowncity.astralib.common.configuration.YamlAstraConfiguration;
import de.unknowncity.astralib.common.configuration.annotation.Config;
import de.unknowncity.astralib.libs.com.fasterxml.jackson.annotation.JsonProperty;
import dev.thezexquex.yasmpp.configuration.settings.CountDownSettings;

@Config(targetFile = "plugins/Yasmpp/countdowns.yml")
public class CountdownConfiguration extends YamlAstraConfiguration {

    @JsonProperty
    private final CountDownSettings countdown = new CountDownSettings();

    public CountdownConfiguration() {

    }

    public CountDownSettings countdown() {
        return countdown;
    }
}
