package dev.thezexquex.yasmpp.configuration.migrations;

import de.unknowncity.astralib.common.config.migration.ConfigMigration;
import de.unknowncity.astralib.common.config.migration.ConfigNodes;
import de.unknowncity.astralib.common.config.migration.MigrationContext;

/**
 * AstraLib 0.9.0 renamed {@code dataBaseDriver} to {@code driver} and dropped {@code sqliteDataBasePath}.
 * For SQLite the database path is now taken from {@code host}.
 */
public class V1DatabaseSettingsMigration implements ConfigMigration {

    @Override
    public int version() {
        return 1;
    }

    @Override
    public String description() {
        return "Update database settings to AstraLib 0.9.0 format";
    }

    @Override
    public void migrate(MigrationContext context) {
        context.edit("config.yml", root -> {
            ConfigNodes.move(root, "database.dataBaseDriver", "database.driver");

            var sqlitePath = ConfigNodes.remove(root, "database.sqliteDataBasePath");
            var driver = ConfigNodes.get(root, "database.driver");
            if (sqlitePath != null && driver != null && driver.asString().equalsIgnoreCase("SQLITE")) {
                ConfigNodes.set(root, "database.host", sqlitePath);
            }
        });
    }
}
