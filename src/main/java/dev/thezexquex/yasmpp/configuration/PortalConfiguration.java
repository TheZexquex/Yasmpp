package dev.thezexquex.yasmpp.configuration;

import dev.thezexquex.yasmpp.modules.lockportal.nether.PortalBlueprint;
import dev.thezexquex.yasmpp.modules.lockportal.nether.PortalState;

public class PortalConfiguration {

    private PortalBlueprint blueprint = null;
    private PortalState state = null;

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
