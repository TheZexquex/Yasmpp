package dev.thezexquex.yasmpp.modules.lockportal.nether;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PortalStateTest {

    private static PortalBlock frame(int x) {
        return new PortalBlock(x, 0, 0, "OBSIDIAN", false, "minecraft:obsidian");
    }

    private static PortalBlock fill(int x) {
        return new PortalBlock(x, 1, 0, "NETHER_PORTAL", true, "minecraft:nether_portal");
    }

    // Indices 0, 2 and 3 are frame blocks, 1 is portal fill
    private final PortalBlueprint blueprint = new PortalBlueprint(List.of(frame(0), fill(1), frame(2), frame(3)));

    @Test
    void portalFillIsNotAFrameIndex() {
        assertEquals(List.of(0, 2, 3), blueprint.frameIndices().boxed().toList());
        assertEquals(3, blueprint.frameBlockCount());
        assertFalse(blueprint.isFrameIndex(1));
        assertFalse(blueprint.isFrameIndex(-1));
        assertFalse(blueprint.isFrameIndex(4));
    }

    @Test
    void completingABlockTwiceIsRejected() {
        var state = new PortalState();

        assertTrue(state.complete(0));
        assertFalse(state.complete(0));
        assertEquals(1, state.completedFrameCount(blueprint));
    }

    @Test
    void portalIsCompleteWhenAllFrameBlocksAreCompleted() {
        var state = new PortalState();
        state.complete(0);
        state.complete(2);

        assertFalse(state.isPortalComplete(blueprint));

        state.complete(3);

        assertTrue(state.isPortalComplete(blueprint));
    }

    @Test
    void retainValidDropsIndicesOutsideTheFrame() {
        var state = new PortalState();
        state.complete(0);
        state.complete(1);
        state.complete(7);

        assertEquals(2, state.retainValid(blueprint));
        assertEquals(java.util.Set.of(0), state.completed());
    }
}
