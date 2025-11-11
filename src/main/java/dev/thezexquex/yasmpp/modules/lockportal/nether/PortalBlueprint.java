package dev.thezexquex.yasmpp.modules.lockportal.nether;

import de.unknowncity.astralib.libs.com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class PortalBlueprint {
    @JsonProperty
    private List<PortalBlock> blocks = List.of();
    @JsonProperty
    private Origin origin;

    public PortalBlueprint() {

    }

    public List<PortalBlock> blocks() {
        return blocks;
    }

    public void blocks(List<PortalBlock> blocks) {
        this.blocks = blocks;
    }
}
