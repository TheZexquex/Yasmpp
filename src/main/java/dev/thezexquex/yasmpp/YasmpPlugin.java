package dev.thezexquex.yasmpp;

import de.unknowncity.astralib.common.config.migration.ConfigMigrator;
import de.unknowncity.astralib.common.database.StandardDataBaseProvider;
import de.unknowncity.astralib.common.hook.HookRegistry;
import de.unknowncity.astralib.common.message.lang.Language;
import de.unknowncity.astralib.common.message.lang.Localization;
import de.unknowncity.astralib.paper.api.hook.PaperPluginHook;
import de.unknowncity.astralib.paper.api.hook.defaulthooks.PlaceholderApiHook;
import de.unknowncity.astralib.paper.api.message.PaperMessenger;
import de.unknowncity.astralib.paper.api.plugin.PaperAstraPlugin;
import dev.chojo.ocular.Configurations;
import dev.chojo.ocular.dataformats.YamlDataFormat;
import dev.chojo.ocular.key.Key;
import dev.thezexquex.yasmpp.commands.*;
import dev.thezexquex.yasmpp.commands.admin.*;
import dev.thezexquex.yasmpp.configuration.CountdownConfiguration;
import dev.thezexquex.yasmpp.configuration.PortalConfiguration;
import dev.thezexquex.yasmpp.configuration.YasmppConfiguration;
import dev.thezexquex.yasmpp.configuration.migrations.V1DatabaseSettingsMigration;
import dev.thezexquex.yasmpp.configuration.migrations.V2RemovePlanCacheSettingsMigration;
import dev.thezexquex.yasmpp.data.database.dao.HomeDao;
import dev.thezexquex.yasmpp.data.database.dao.HomeSlotDao;
import dev.thezexquex.yasmpp.data.database.dao.LocationDao;
import dev.thezexquex.yasmpp.data.service.HomeService;
import dev.thezexquex.yasmpp.data.service.LocationService;
import dev.thezexquex.yasmpp.data.service.SmpPlayerService;
import dev.thezexquex.yasmpp.gamesettings.BooleanGameSetting;
import dev.thezexquex.yasmpp.gamesettings.GameSettingRegistry;
import dev.thezexquex.yasmpp.gamesettings.IntegerGameSetting;
import dev.thezexquex.yasmpp.hooks.PlanHook;
import dev.thezexquex.yasmpp.modules.blockdamage.ExplosionBlockDamageListener;
import dev.thezexquex.yasmpp.modules.chat.ChatListener;
import dev.thezexquex.yasmpp.modules.joinleavemessage.PlayerJoinListener;
import dev.thezexquex.yasmpp.modules.joinleavemessage.PlayerQuitListener;
import dev.thezexquex.yasmpp.modules.lockportal.LockPortalListener;
import dev.thezexquex.yasmpp.modules.lockportal.nether.NetherPortalManager;
import dev.thezexquex.yasmpp.modules.lockportal.nether.PlayerInteractListener;
import dev.thezexquex.yasmpp.modules.mobileworkstations.WorkstationInteractListener;
import dev.thezexquex.yasmpp.modules.respawn.RespawnListener;
import dev.thezexquex.yasmpp.modules.signedit.SignEditListener;
import dev.thezexquex.yasmpp.modules.spawnelytra.ElytraManager;
import dev.thezexquex.yasmpp.modules.spawnelytra.listener.*;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.incendo.cloud.setting.ManagerSetting;

import java.nio.file.Path;
import java.util.logging.Logger;

public class YasmpPlugin extends PaperAstraPlugin {
    public static final Logger LOGGER = Logger.getLogger("Yasmpp");

    private Configurations<YasmppConfiguration> mainConfiguration;
    private Configurations<CountdownConfiguration> countdownConfiguration;
    private Configurations<PortalConfiguration> portalConfiguration;

    private PaperMessenger messenger;
    private LocationService locationService;
    private HomeService homeService;
    private SmpPlayerService smpPlayerService;
    private ElytraManager elytraManager;
    private NetherPortalManager netherPortalManager;
    private GameSettingRegistry gameSettingRegistry;

    @Override
    public void onPluginEnable() {
        gameSettingRegistry = new GameSettingRegistry();
        reloadPlugin();
        initDataServices();
        applyListeners();
        // Lets flags precede optional arguments, e.g. "/ontime --no-afk" instead of parsing the flag as player name
        commandManager.settings().set(ManagerSetting.LIBERAL_FLAG_PARSING, true);
        hookRegistry.register(new PlanHook(this));

        registerGameSettings();

        applyCommands();
        elytraManager = new ElytraManager(this);
        netherPortalManager = new NetherPortalManager(this);
        netherPortalManager.respawnPortalOnServerStart();

        Permissions.ALL_PERMISSIONS.forEach(permission -> Bukkit.getPluginManager().addPermission(new Permission(permission)));
    }

    private void registerGameSettings() {
        gameSettingRegistry.register(new BooleanGameSetting("lock-nether", "Lock Nether portals", () -> mainConfiguration.main().general().portals().lockNether(), (val) -> mainConfiguration.main().general().portals().lockNether(val)));
        gameSettingRegistry.register(new BooleanGameSetting("lock-end", "Lock End portals", () -> mainConfiguration.main().general().portals().lockEnd(), (val) -> mainConfiguration.main().general().portals().lockEnd(val)));
        gameSettingRegistry.register(new BooleanGameSetting("block-damage-tnt", "TNT block damage", () -> mainConfiguration.main().general().explosionDamage().doTntDamage(), (val) -> mainConfiguration.main().general().explosionDamage().setDoTntDamage(val)));
        gameSettingRegistry.register(new BooleanGameSetting("block-damage-creeper", "Creeper block damage", () -> mainConfiguration.main().general().explosionDamage().doCreeperDamage(), (val) -> mainConfiguration.main().general().explosionDamage().setDoCreeperDamage(val)));
        gameSettingRegistry.register(new IntegerGameSetting("elytra-max-boosts", "Max boosts for elytra at spawn", () -> mainConfiguration.main().general().spawnElytra().maxBoosts(), (val) -> mainConfiguration.main().general().spawnElytra().setMaxBoosts(val)));
        gameSettingRegistry.register(new IntegerGameSetting("elytra-radius", "Radius for elytra boosts at spawn", () -> mainConfiguration.main().general().spawnElytra().radius(), (val) -> mainConfiguration.main().general().spawnElytra().setRadius(val)));
        gameSettingRegistry.register(new BooleanGameSetting("tp-cancel-on-move", "Cancel teleport on move", () -> mainConfiguration.main().teleport().cancelOnMove(), (val) -> mainConfiguration.main().teleport().setCancelOnMove(val)));
        gameSettingRegistry.register(new IntegerGameSetting("tp-cooldown", "Teleport cooldown", () -> mainConfiguration.main().teleport().teleportCoolDownInSeconds(), (val) -> mainConfiguration.main().teleport().setTeleportCoolDownInSeconds(val)));
        gameSettingRegistry.register(new BooleanGameSetting("tp-bypassed-by-permission", "Permission bypasses teleport cooldown", () -> mainConfiguration.main().teleport().permissionBypassesCoolDown(), (val) -> mainConfiguration.main().teleport().setPermissionBypassesCoolDown(val)));
    }

    @Override
    public void onPluginReload() {
        reloadPlugin();
    }

    @Override
    public void onPluginDisable() {
        if (netherPortalManager != null) {
            netherPortalManager.onDisable();
        }
    }

    public void reloadPlugin() {
        loadConfigAndMessages();
    }

    public void loadConfigAndMessages() {

        saveDefaultResource("lang/de_DE.yml", Path.of("lang/de_DE.yml"));

        var localization = Localization.builder(getDataPath().resolve("lang"))
                .withLogger(getLogger())
                .buildAndLoad();

        var papiHook = hookRegistry.getRegistered(PlaceholderApiHook.class).orElse(null);

        this.messenger = PaperMessenger.builder(localization, getPluginMeta())
                .withPlaceHolderAPI(papiHook)
                .withDefaultLanguage(Language.GERMAN)
                .build();

        // Migrations must run before saving the default config / loading the configs
        var migrator = ConfigMigrator.builder(getDataPath(), getLogger()).migrations(
                new V1DatabaseSettingsMigration(),
                new V2RemovePlanCacheSettingsMigration()
        ).build();

        migrator.migrate();

        var mainKey = Key.builder(Path.of("config.yml"), YasmppConfiguration::new).build();
        var countdownKey = Key.builder(Path.of("countdowns.yml"), CountdownConfiguration::new).build();
        var portalKey = Key.builder(Path.of("portal.yml"), PortalConfiguration::new).build();

        var basePath = getDataPath();

        mainConfiguration = Configurations.builder(mainKey, new YamlDataFormat()).setBase(basePath).build();
        countdownConfiguration = Configurations.builder(countdownKey, new YamlDataFormat()).setBase(basePath).build();
        portalConfiguration = Configurations.builder(portalKey, new YamlDataFormat()).setBase(basePath).build();
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
        pluginManager.registerEvents(new ElytraBoostListener(this), this);

        // Lock end
        pluginManager.registerEvents(new LockPortalListener(this), this);

        pluginManager.registerEvents(new ChatListener(this), this);

        // Nether Portal
        pluginManager.registerEvents(new PlayerInteractListener(this), this);

        pluginManager.registerEvents(new SignEditListener(), this);
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

        new PortalCommand(this).apply(commandManager);
        new CaptchaCommand(this).apply(commandManager);
        new SysInfoCommand(this).apply(commandManager);
    }

    private void initDataServices() {
        var queryConfig = StandardDataBaseProvider.updateAndConnectToDataBase(
                mainConfiguration.main().database(),
                getClassLoader(),
                getDataPath()
        );

        var locationDao = new LocationDao(queryConfig);
        locationService = new LocationService(locationDao);
        locationService.loadLocations();

        var homeDao = new HomeDao(queryConfig);
        var homeSlotDao = new HomeSlotDao(queryConfig);
        smpPlayerService = new SmpPlayerService(this);
        homeService = new HomeService(homeDao, homeSlotDao, smpPlayerService);
    }

    public HookRegistry<PaperAstraPlugin, PaperPluginHook> hookRegistry() {
        return hookRegistry;
    }

    public Configurations<YasmppConfiguration> configuration() {
        return mainConfiguration;
    }

    public Configurations<CountdownConfiguration> countdownConfiguration() {
        return countdownConfiguration;
    }

    public Configurations<PortalConfiguration> portalConfiguration() {
        return portalConfiguration;
    }

    public PaperMessenger messenger() {
        return messenger;
    }

    /**
     * Language messages for the given receiver are resolved in, needed when building components ahead of sending.
     */
    public Language language(CommandSender sender) {
        if (languageService == null) {
            return Language.GERMAN;
        }
        return sender instanceof Player player ? languageService.getPlayerLanguage(player) : languageService.getDefaultLanguage();
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

    public NetherPortalManager netherPortalManager() {
        return netherPortalManager;
    }

    public GameSettingRegistry gameSettingRegistry() {
        return gameSettingRegistry;
    }
}