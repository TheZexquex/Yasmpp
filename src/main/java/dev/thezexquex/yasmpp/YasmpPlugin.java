package dev.thezexquex.yasmpp;

import de.unknowncity.astralib.common.database.StandardDataBaseProvider;
import de.unknowncity.astralib.common.hook.HookRegistry;
import de.unknowncity.astralib.common.message.lang.Language;
import de.unknowncity.astralib.common.message.lang.Localization;
import de.unknowncity.astralib.paper.api.hook.PaperPluginHook;
import de.unknowncity.astralib.paper.api.hook.defaulthooks.PlaceholderApiHook;
import de.unknowncity.astralib.paper.api.message.PaperMessenger;
import de.unknowncity.astralib.paper.api.plugin.PaperAstraPlugin;
import dev.thezexquex.yasmpp.commands.*;
import dev.thezexquex.yasmpp.commands.admin.*;
import dev.thezexquex.yasmpp.configuration.CountdownConfiguration;
import dev.thezexquex.yasmpp.configuration.PortalConfiguration;
import dev.thezexquex.yasmpp.configuration.YasmppConfiguration;
import dev.thezexquex.yasmpp.data.database.dao.HomeDao;
import dev.thezexquex.yasmpp.data.database.dao.HomeSlotDao;
import dev.thezexquex.yasmpp.data.database.dao.LocationDao;
import dev.thezexquex.yasmpp.data.service.HomeService;
import dev.thezexquex.yasmpp.data.service.LocationService;
import dev.thezexquex.yasmpp.data.service.SmpPlayerService;
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
import dev.thezexquex.yasmpp.modules.spawnelytra.ElytraManager;
import dev.thezexquex.yasmpp.modules.spawnelytra.listener.*;
import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;

import java.nio.file.Path;
import java.util.logging.Logger;

public class YasmpPlugin extends PaperAstraPlugin {
    private YasmppConfiguration configuration;
    private CountdownConfiguration countdownConfiguration;
    private PortalConfiguration portalConfiguration;
    private PaperMessenger messenger;
    private LocationService locationService;
    private HomeService homeService;
    private SmpPlayerService smpPlayerService;
    private ElytraManager elytraManager;
    private NetherPortalManager netherPortalManager;
    public static final Logger LOGGER = Logger.getLogger("Yasmpp");

    @Override
    public void onPluginEnable() {
        reloadPlugin();
        initDataServices();
        applyListeners();
        //new StackSizeChanger(this).changeAllItemStackSizes();
        hookRegistry.register(new PlanHook(this));
        applyCommands();
        elytraManager = new ElytraManager(this);
        netherPortalManager = new NetherPortalManager(this);
        netherPortalManager.respawnPortalOnServerStart();

        Permissions.ALL_PERMISSIONS.forEach(permission -> Bukkit.getPluginManager().addPermission(new Permission(permission)));
    }

    @Override
    public void onPluginDisable() {
        netherPortalManager.onDisable();
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

        this.messenger = PaperMessenger.builder(localization, getPluginMeta())
                .withPlaceHolderAPI(papiHook)
                .withDefaultLanguage(Language.GERMAN)
                .build();

        var configOpt = YasmppConfiguration.loadFromFile(YasmppConfiguration.class);

        this.configuration = configOpt.orElseGet(YasmppConfiguration::new);
        this.configuration.save();

        var countdownConfigOpt = CountdownConfiguration.loadFromFile(CountdownConfiguration.class);
        this.countdownConfiguration = countdownConfigOpt.orElseGet(CountdownConfiguration::new);
        this.countdownConfiguration.save();

        var portalConfigOpt = PortalConfiguration.loadFromFile(PortalConfiguration.class);
        this.portalConfiguration = portalConfigOpt.orElseGet(PortalConfiguration::new);
        this.portalConfiguration.save();
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

        new PortalCommand(this).apply(commandManager);
        new CaptchaCommand(this).apply(commandManager);
    }

    private void initDataServices() {
        var queryConfig = StandardDataBaseProvider.updateAndConnectToDataBase(
                configuration.database(),
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

    public YasmppConfiguration configuration() {
        return configuration;
    }

    public CountdownConfiguration countdownConfiguration() {
        return countdownConfiguration;
    }

    public PortalConfiguration portalConfiguration() {
        return portalConfiguration;
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

    public NetherPortalManager netherPortalManager() {
        return netherPortalManager;
    }
}