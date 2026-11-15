package dev.thezexquex.yasmpp.modules.lockportal.nether;

import java.util.List;
import java.util.stream.IntStream;

public record PortalBlueprint(List<PortalBlock> blocks) {
    public PortalBlueprint(List<PortalBlock> blocks) {
        this.blocks = List.copyOf(blocks);
    }

    public boolean hasBlocks() {
        return blocks != null && !blocks.isEmpty();
    }

    public IntStream frameIndices() {
        return IntStream.range(0, blocks.size()).filter(this::isFrameIndex);
    }

    public long frameBlockCount() {
        return frameIndices().count();
    }

    public boolean isFrameIndex(int index) {
        return index >= 0 && index < blocks.size() && blocks.get(index).isFrameBlock();
    }

    public PortalBlock block(int index) {
        return blocks.get(index);
    }

    /**
     * @return indices of blocks whose material is unknown to the server
     */
    public List<Integer> invalidBlockIndices() {
        return IntStream.range(0, blocks.size())
                .filter(index -> blocks.get(index).getMaterial() == null)
                .boxed()
                .toList();
    }
}
