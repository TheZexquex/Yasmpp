package dev.thezexquex.yasmpp.configuration;

import de.unknowncity.astralib.common.configuration.YamlAstraConfiguration;
import de.unknowncity.astralib.common.configuration.annotation.Config;
import de.unknowncity.astralib.libs.com.fasterxml.jackson.annotation.JsonProperty;
import dev.thezexquex.yasmpp.modules.lockportal.nether.PortalBlueprint;
import dev.thezexquex.yasmpp.modules.lockportal.nether.PortalState;

@Config(targetFile = "plugins/Yasmpp/portal.yml")
public class PortalConfiguration extends YamlAstraConfiguration {

    @JsonProperty
    private PortalBlueprint blueprint = null;
    @JsonProperty
    private PortalState state = null;
    public PortalConfiguration() {

    }

    public PortalBlueprint blueprint() {
        return blueprint;
    }

    public void blueprint(PortalBlueprint blueprint) {
        this.blueprint = blueprint;
    }

    public PortalState state() {
        return state;
    }

    public void state(PortalState state) {
        this.state = state;
    }
}
