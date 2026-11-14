package dev.thezexquex.yasmpp.configuration;

import de.unknowncity.astralib.common.config.DatabaseSettings;
import dev.thezexquex.yasmpp.configuration.settings.ChatSettings;
import dev.thezexquex.yasmpp.configuration.settings.GeneralSettings;
import dev.thezexquex.yasmpp.configuration.settings.HomeSettings;
import dev.thezexquex.yasmpp.configuration.settings.SysInfoSettings;
import dev.thezexquex.yasmpp.configuration.settings.TeleportSettings;

public class YasmppConfiguration {

    private DatabaseSettings database = new DatabaseSettings();
    private GeneralSettings general = new GeneralSettings();
    private TeleportSettings teleport = new TeleportSettings();
    private final ChatSettings chat = new ChatSettings();
    private HomeSettings homes = new HomeSettings();
    private SysInfoSettings sysInfo = new SysInfoSettings();

    public GeneralSettings general() {
        return general;
    }

    public TeleportSettings teleport() {
        return teleport;
    }

    public ChatSettings chat() {
        return chat;
    }

    public DatabaseSettings database() {
        return database;
    }

    public HomeSettings homes() {
        return homes;
    }

    public SysInfoSettings sysInfo() {
        return sysInfo;
    }
}