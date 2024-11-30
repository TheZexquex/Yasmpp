package dev.thezexquex.yasmpp;

import de.unknowncity.astralib.common.database.StandardDataBaseProvider;
import de.unknowncity.astralib.common.message.lang.Language;
import de.unknowncity.astralib.common.message.lang.Localization;
import de.unknowncity.astralib.paper.api.hook.defaulthooks.PlaceholderApiHook;
import de.unknowncity.astralib.paper.api.message.PaperMessenger;
import de.unknowncity.astralib.paper.api.plugin.PaperAstraPlugin;
import dev.thezexquex.yasmpp.commands.*;
import dev.thezexquex.yasmpp.commands.admin.GameCommand;
import dev.thezexquex.yasmpp.commands.admin.GameModeCommand;
import dev.thezexquex.yasmpp.commands.admin.SpeedCommand;
import dev.thezexquex.yasmpp.configuration.YasmppConfiguration;
import dev.thezexquex.yasmpp.data.database.dao.location.impl.sqlite.SqliteHomeDao;
import dev.thezexquex.yasmpp.data.database.dao.location.impl.sqlite.SqliteLocationDao;
import dev.thezexquex.yasmpp.data.service.HomeService;
import dev.thezexquex.yasmpp.data.service.LocationService;
import dev.thezexquex.yasmpp.data.service.SmpPlayerService;
import dev.thezexquex.yasmpp.modules.blockdamage.ExplosionBlockDamageListener;
import dev.thezexquex.yasmpp.modules.chat.ChatListener;
import dev.thezexquex.yasmpp.modules.joinleavemessage.PlayerJoinListener;
import dev.thezexquex.yasmpp.modules.joinleavemessage.PlayerQuitListener;
import dev.thezexquex.yasmpp.modules.lockend.LockEndListener;
import dev.thezexquex.yasmpp.modules.mobileworkstations.WorkstationInteractListener;
import dev.thezexquex.yasmpp.modules.respawn.RespawnListener;
import dev.thezexquex.yasmpp.modules.spawnelytra.ElytraManager;
import dev.thezexquex.yasmpp.modules.spawnelytra.listener.*;

import java.nio.file.Path;

public class YasmpPlugin extends PaperAstraPlugin {
    private YasmppConfiguration configuration;
    private PaperMessenger messenger;
    private LocationService locationService;
    private HomeService homeService;
    private SmpPlayerService smpPlayerService;
    private ElytraManager elytraManager;

    @Override
    public void onPluginEnable() {
        reloadPlugin();
        initDataServices();
        applyListeners();
        //new StackSizeChanger(this).changeAllItemStackSizes();
        applyCommands();
        elytraManager = new ElytraManager(this);
    }

    public void reloadPlugin() {
        loadConfigAndMessages();
    }

    public void loadConfigAndMessages() {

        saveDefaultResource("lang/de_DE.yml", Path.of("lang/de_DE.yml"));

        var localization = Localization.builder(getDataPath().resolve("lang"))
                .withLogger(getLogger())
                .buildAndLoad();

        var papiHook = hookRegistry.getRegistered(PlaceholderApiHook.class);

        this.messenger = PaperMessenger.builder(localization)
                .withPlaceHolderAPI(papiHook)
                .withDefaultLanguage(Language.GERMAN)
                .build();

        var configOpt = YasmppConfiguration.loadFromFile(YasmppConfiguration.class);

        this.configuration = configOpt.orElseGet(YasmppConfiguration::new);
        this.configuration.save();
    }

    private void applyListeners() {
        var pluginManager = this.getServer().getPluginManager();

        pluginManager.registerEvents(new WorkstationInteractListener(this), this);
        pluginManager.registerEvents(new ExplosionBlockDamageListener(this), this);

        pluginManager.registerEvents(new PlayerJoinListener(this), this);
        pluginManager.registerEvents(new PlayerQuitListener(this), this);

        pluginManager.registerEvents(new RespawnListener(this), this);

        // Spawn elytra
        pluginManager.registerEvents(new ArmorChangeListener(this), this);
        pluginManager.registerEvents(new ElytraSneakListener(this), this);
        pluginManager.registerEvents(new ElytraToggleGlideListener(this), this);
        pluginManager.registerEvents(new ElytraToggleOnVehicleEnterListener(this), this);
        pluginManager.registerEvents(new FlightAttemptListener(this), this);
        pluginManager.registerEvents(new PlayerMoveListener(this), this);
        pluginManager.registerEvents(new ElytraSavetyPlayerJoinListener(this), this);

        // Lock end
        pluginManager.registerEvents(new LockEndListener(this), this);

        //Teleport
        pluginManager.registerEvents(new ChatListener(this), this);
    }

    private void applyCommands() {
        new HomeCommand(this).apply(commandManager);
        new SpawnCommand(this).apply(commandManager);
        new SetSpawnCommand(this).apply(commandManager);

        new GameModeCommand(this).apply(commandManager);
        new SpeedCommand(this).apply(commandManager);

        new GameCommand(this).apply(commandManager);
        new GameSettingsCommand(this).apply(commandManager);

        new RestartCommand(this).apply(commandManager);
        new ReloadCommand(this).apply(commandManager);
    }

    private void initDataServices() {
        var queryConfig = StandardDataBaseProvider.updateAndConnectToDataBase(
                configuration.modernDataBaseSetting(),
                getClassLoader(),
                getDataPath()
        );

        var locationDao = new SqliteLocationDao(queryConfig);
        locationService = new LocationService(locationDao);

        var homeDao = new SqliteHomeDao(queryConfig);
        homeService = new HomeService(homeDao);
        smpPlayerService = new SmpPlayerService(this);
    }

    public YasmppConfiguration configuration() {
        return configuration;
    }

    public PaperMessenger messenger() {
        return messenger;
    }

    public LocationService locationService() {
        return locationService;
    }

    public HomeService homeService() {
        return homeService;
    }

    public SmpPlayerService smpPlayerService() {
        return smpPlayerService;
    }

    public ElytraManager elytraManager() {
        return elytraManager;
    }
}