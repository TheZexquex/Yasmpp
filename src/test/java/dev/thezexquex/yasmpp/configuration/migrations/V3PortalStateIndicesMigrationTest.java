package dev.thezexquex.yasmpp.configuration.migrations;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class V3PortalStateIndicesMigrationTest {

    private static final String BLUEPRINT = """
            "blueprint": {"blocks": [
              {"x": 0, "y": 0, "z": 0, "material": "OBSIDIAN", "isPortalFill": false, "blockData": "minecraft:obsidian"},
              {"x": 0, "y": 1, "z": 0, "material": "NETHER_PORTAL", "isPortalFill": true, "blockData": "minecraft:nether_portal"},
              {"x": 0, "y": 2, "z": 0, "material": "OBSIDIAN", "isPortalFill": false, "blockData": "minecraft:obsidian"}
            ]}""";

    private static ObjectNode parse(String json) {
        return (ObjectNode) JsonMapper.builder().build().readTree(json);
    }

    private static String block(int y, String material) {
        return "{\"x\": 0, \"y\": " + y + ", \"z\": 0, \"material\": \"" + material + "\", \"isPortalFill\": false, \"blockData\": \"\"}";
    }

    private static List<Integer> completed(ObjectNode root) {
        var result = new ArrayList<Integer>();
        var node = root.path("state").path("completed");
        for (int i = 0; i < node.size(); i++) {
            result.add(node.get(i).asInt());
        }
        return result;
    }

    @Test
    void completedBlocksAreMatchedByPositionAndMaterial() {
        // Old list in a different order than the blueprint
        var root = parse("{" + BLUEPRINT + ", \"state\": {"
                + "\"origin\": {\"world\": \"world\", \"x\": 1, \"y\": 2, \"z\": 3},"
                + "\"blocks\": ["
                + "{\"block\": " + block(2, "OBSIDIAN") + ", \"completed\": true},"
                + "{\"block\": " + block(0, "OBSIDIAN") + ", \"completed\": false}"
                + "]}}");

        assertEquals(0, V3PortalStateIndicesMigration.migratePortalState(root));
        assertEquals(List.of(2), completed(root));
        assertFalse(root.path("state").has("blocks"));
        assertEquals("world", root.path("state").path("origin").path("world").asString());
    }

    @Test
    void blocksNotInTheBlueprintAreDropped() {
        var root = parse("{" + BLUEPRINT + ", \"state\": {\"blocks\": ["
                + "{\"block\": " + block(5, "OBSIDIAN") + ", \"completed\": true},"
                + "{\"block\": " + block(0, "CRYING_OBSIDIAN") + ", \"completed\": true},"
                + "{\"block\": " + block(0, "OBSIDIAN") + ", \"completed\": true}"
                + "]}}");

        assertEquals(2, V3PortalStateIndicesMigration.migratePortalState(root));
        assertEquals(List.of(0), completed(root));
    }

    @Test
    void fileWithoutOldStateIsUntouched() {
        var root = parse("{" + BLUEPRINT + "}");

        assertEquals(0, V3PortalStateIndicesMigration.migratePortalState(root));
        assertFalse(root.has("state"));
    }
}
