package dev.thezexquex.yasmpp.modules.lockportal.nether;

import java.util.List;

public class PortalBlueprint {
    private List<PortalBlock> blocks = List.of();
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
