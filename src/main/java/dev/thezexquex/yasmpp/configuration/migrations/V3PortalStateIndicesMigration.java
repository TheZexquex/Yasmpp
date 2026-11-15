package dev.thezexquex.yasmpp.configuration.migrations;

import de.unknowncity.astralib.common.config.migration.ConfigMigration;
import de.unknowncity.astralib.common.config.migration.ConfigNodes;
import de.unknowncity.astralib.common.config.migration.MigrationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ObjectNode;

/**
 * The portal state no longer stores a copy of every blueprint block, only the indices of the completed ones.
 */
public class V3PortalStateIndicesMigration implements ConfigMigration {

    @Override
    public int version() {
        return 3;
    }

    @Override
    public String description() {
        return "Store completed portal blocks as blueprint indices";
    }

    @Override
    public void migrate(MigrationContext context) {
        context.edit("portal.yml", root -> {
            var unmatched = migratePortalState(root);
            if (unmatched > 0) {
                context.logger().warning(unmatched + " completed portal blocks do not match the blueprint and were dropped.");
            }
        });
    }

    /**
     * @return the number of completed blocks that could not be matched to a blueprint block
     */
    static int migratePortalState(ObjectNode root) {
        var oldBlocks = ConfigNodes.get(root, "state.blocks");
        if (oldBlocks == null || !oldBlocks.isArray()) {
            return 0;
        }
        var blueprintBlocks = ConfigNodes.get(root, "blueprint.blocks");

        var state = (ObjectNode) ConfigNodes.get(root, "state");
        var completed = state.putArray("completed");
        int unmatched = 0;

        for (int i = 0; i < oldBlocks.size(); i++) {
            var oldBlock = oldBlocks.get(i);
            if (!oldBlock.path("completed").asBoolean()) {
                continue;
            }
            // Matched by position and material, since the order of the old list is not guaranteed to match
            var index = indexOf(blueprintBlocks, oldBlock.path("block"));
            if (index < 0) {
                unmatched++;
            } else {
                completed.add(index);
            }
        }

        state.remove("blocks");
        return unmatched;
    }

    private static int indexOf(JsonNode blueprintBlocks, JsonNode block) {
        if (blueprintBlocks == null || !blueprintBlocks.isArray()) {
            return -1;
        }
        for (int i = 0; i < blueprintBlocks.size(); i++) {
            var candidate = blueprintBlocks.get(i);
            if (candidate.path("x").asInt() == block.path("x").asInt()
                    && candidate.path("y").asInt() == block.path("y").asInt()
                    && candidate.path("z").asInt() == block.path("z").asInt()
                    && candidate.path("material").asString().equals(block.path("material").asString())) {
                return i;
            }
        }
        return -1;
    }
}
