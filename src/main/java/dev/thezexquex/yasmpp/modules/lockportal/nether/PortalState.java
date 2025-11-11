package dev.thezexquex.yasmpp.modules.lockportal.nether;

import de.unknowncity.astralib.libs.com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class PortalState {
    @JsonProperty
    private Origin origin;
    @JsonProperty
    private List<PortalBlockState> blocks;

    public PortalState() {

    }

    public PortalState(Origin origin, List<PortalBlockState> blocks) {
        this.origin = origin;
        this.blocks = blocks;
    }

    public Origin origin() {
        return origin;
    }

    public void origin(Origin origin) {
        this.origin = origin;
    }

    public List<PortalBlockState> blocks() {
        return blocks;
    }

    public PortalBlockState getBlock(PortalBlock block) {
        return blocks.stream().filter(portalBlockState -> portalBlockState.block().equals(block)).findFirst().orElse(null);
    }
}
