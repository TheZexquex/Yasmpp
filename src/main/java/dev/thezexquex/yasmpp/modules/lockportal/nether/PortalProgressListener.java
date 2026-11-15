package dev.thezexquex.yasmpp.modules.lockportal.nether;

import org.bukkit.Location;

/**
 * Notified by {@link NetherPortalManager} about changes of the portal progress.
 */
public interface PortalProgressListener {

    /**
     * The portal is shown and its progress changed, or it was spawned.
     */
    default void onProgress(long completed, long total) {
    }

    /**
     * The last frame block was placed. Not called for portals that were already complete when they were loaded.
     */
    default void onCompleted(Location origin, PortalBlueprint blueprint) {
    }

    /**
     * The portal is no longer shown, e.g. because it was despawned, rescanned or loaded as complete.
     */
    default void onHidden() {
    }
}
