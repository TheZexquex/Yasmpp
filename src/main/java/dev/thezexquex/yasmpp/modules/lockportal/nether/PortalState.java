package dev.thezexquex.yasmpp.modules.lockportal.nether;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Progress of a spawned portal. Blocks are referenced by their index in {@link PortalBlueprint#blocks()},
 * so the blueprint stays the only place where the portal's shape is stored.
 */
public class PortalState {
    private Origin origin;
    private Set<Integer> completed = new HashSet<>();

    public PortalState() {

    }

    public PortalState(Origin origin) {
        this.origin = origin;
    }

    public Origin origin() {
        return origin;
    }

    public void origin(Origin origin) {
        this.origin = origin;
    }

    public Set<Integer> completed() {
        return Collections.unmodifiableSet(completed);
    }

    public boolean isCompleted(int index) {
        return completed.contains(index);
    }

    /**
     * @return false if the block was already completed
     */
    public boolean complete(int index) {
        return completed.add(index);
    }

    public boolean isPortalComplete(PortalBlueprint blueprint) {
        return blueprint.frameIndices().allMatch(completed::contains);
    }

    public long completedFrameCount(PortalBlueprint blueprint) {
        return blueprint.frameIndices().filter(completed::contains).count();
    }

    /**
     * Removes indices that do not point to a frame block of the blueprint, e.g. after editing portal.yml by hand.
     *
     * @return the number of removed indices
     */
    public int retainValid(PortalBlueprint blueprint) {
        var before = completed.size();
        completed.removeIf(index -> !blueprint.isFrameIndex(index));
        return before - completed.size();
    }
}
