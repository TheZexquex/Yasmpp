package dev.thezexquex.yasmpp.configuration.migrations;

import de.unknowncity.astralib.common.config.migration.ConfigMigration;
import de.unknowncity.astralib.common.config.migration.ConfigNodes;
import de.unknowncity.astralib.common.config.migration.MigrationContext;

/**
 * Plan data is no longer cached periodically, and the server is identified by Plan's server UUID
 * instead of its name, so both settings are obsolete.
 */
public class V2RemovePlanCacheSettingsMigration implements ConfigMigration {

    @Override
    public int version() {
        return 2;
    }

    @Override
    public String description() {
        return "Remove obsolete Plan cache settings";
    }

    @Override
    public void migrate(MigrationContext context) {
        context.edit("config.yml", root -> {
            ConfigNodes.remove(root, "general.planServerName");
            ConfigNodes.remove(root, "general.planDataRefreshInterval");
        });
    }
}
