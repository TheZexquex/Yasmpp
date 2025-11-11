package dev.thezexquex.yasmpp.modules.lockportal.nether;

import de.unknowncity.astralib.libs.com.fasterxml.jackson.annotation.JsonProperty;

public class PortalBlockState {
    @JsonProperty
    private PortalBlock block;
    @JsonProperty
    private boolean completed;

    public PortalBlockState() {

    }

    public PortalBlockState(PortalBlock block) {
        this.block = block;
        completed = false;
    }

    public PortalBlock block() {
        return block;
    }

    public boolean completed() {
        return completed;
    }

    public void complete() {
        completed = true;
    }
}
