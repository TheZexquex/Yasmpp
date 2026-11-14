package dev.thezexquex.yasmpp.modules.lockportal.nether;

public class PortalBlockState {
    private PortalBlock block;
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
