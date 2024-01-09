package dev.thezexquex.yasmpp;

import cloud.commandframework.CommandManager;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import com.zaxxer.hikari.HikariDataSource;
import de.chojo.sadu.wrapper.QueryBuilderConfig;
import dev.thezexquex.yasmpp.commands.*;
import dev.thezexquex.yasmpp.commands.admin.GameCommand;
import dev.thezexquex.yasmpp.core.configuration.ConfigurationLoader;
import dev.thezexquex.yasmpp.core.configuration.Configuration;
import dev.thezexquex.yasmpp.core.data.database.DataBaseProvider;
import dev.thezexquex.yasmpp.core.data.database.DataBaseUpdater;
import dev.thezexquex.yasmpp.core.data.database.dao.location.home.SqliteHomeDao;
import dev.thezexquex.yasmpp.core.data.database.dao.location.special.SqliteLocationDao;
import dev.thezexquex.yasmpp.core.data.service.HomeService;
import dev.thezexquex.yasmpp.core.data.service.LocationService;
import dev.thezexquex.yasmpp.core.data.service.SmpPlayerService;
import dev.thezexquex.yasmpp.core.hooks.PluginHookService;
import dev.thezexquex.yasmpp.core.message.Messenger;
import dev.thezexquex.yasmpp.modules.blockdamage.ExplosionBlockDamageListener;
import dev.thezexquex.yasmpp.modules.chat.ChatListener;
import dev.thezexquex.yasmpp.modules.joinleavemessage.PlayerJoinListener;
import dev.thezexquex.yasmpp.modules.joinleavemessage.PlayerQuitListener;
import dev.thezexquex.yasmpp.modules.lockend.LockEndListener;
import dev.thezexquex.yasmpp.modules.mobileworkstations.WorkstationInteractListener;
import dev.thezexquex.yasmpp.modules.respawn.RespawnListener;
import dev.thezexquex.yasmpp.modules.spawnelytra.*;
import dev.thezexquex.yasmpp.modules.spawnelytra.listener.*;
import dev.thezexquex.yasmpp.modules.teleport.CancelTeleportListener;
import dev.thezexquex.yasmpp.modules.teleport.TeleportQueue;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.logging.Level;

public class YasmpPlugin extends JavaPlugin {
    private Configuration configuration;
    private Messenger messenger;
    private PluginHookService pluginHookService;
    private DataBaseUpdater dataBaseUpdater;
    private DataBaseProvider dataBaseProvider;
    private HikariDataSource dataSource;
    private ConfigurationLoader configurationLoader;
    private CommandManager<CommandSender> commandManager;
    private LocationService locationService;
    private HomeService homeService;
    private SmpPlayerService smpPlayerService;
    private ElytraManager elytraManager;
    private TeleportQueue teleportQueue;

    @Override
    public void onEnable() {
        QueryBuilderConfig.setDefault(QueryBuilderConfig.builder()
                .withExceptionHandler(err -> {
                    getLogger().log(Level.SEVERE, "An error occured during a database request", err);
                })
                .withExecutor(Executors.newCachedThreadPool())
                .build());

        registerListeners();
        reloadPlugin();

        initDataServices();

        //new StackSizeChanger(this).changeAllItemStackSizes();
    }

    public void reloadPlugin() {
        this.pluginHookService = new PluginHookService(this.getServer());

        configurationLoader = new ConfigurationLoader(this);

        configurationLoader.saveDefaultConfigs();
        configurationLoader.intiConfigurationLoader();

        var configurationRootNode = configurationLoader.loadConfiguration();
        var messageRootNode = configurationLoader.loadMessageConfiguration();

        try {
            configuration = configurationRootNode.get(Configuration.class)  ;
        } catch (SerializationException e) {
            this.getLogger().log(Level.SEVERE, "Failed to load config.conf", e);
        }

        messenger = new Messenger(this, messageRootNode);

        updateAndConnectToDatabase();

        initCommandManager();
        registerCommands();

        teleportQueue = new TeleportQueue();

        elytraManager = new ElytraManager(this);
    }

    private void registerListeners() {
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
        pluginManager.registerEvents(new CancelTeleportListener(this), this);

        pluginManager.registerEvents(new ChatListener(this), this);

    }

    private void registerCommands() {
        new HomeCommand(this).register(commandManager);
        new SetSpawnCommand(this).register(commandManager);
        new SpawnCommand(this).register(commandManager);
        new ReloadCommand(this).register(commandManager);
        new RestartCommand(this).register(commandManager);
        new GameSettingsCommand(this).register(commandManager);

        //new GameModeCommand(this).register(commandManager);
        //new SpeedCommand(this).register(commandManager);
        new GameCommand(this).register(commandManager);
    }

    private void updateAndConnectToDatabase() {
        var databasePath = this.getDataFolder().toPath().resolve(Path.of("db.sqlite"));
        this.dataBaseProvider = new DataBaseProvider(databasePath);
        this.dataSource = this.dataBaseProvider.createDataSource();

        this.dataBaseUpdater = new DataBaseUpdater(dataSource);
        try {
            dataBaseUpdater.update();
        } catch (IOException | SQLException e) {
            this.getLogger().log(Level.SEVERE, "Failed to update database", e);
            this.getServer().getPluginManager().disablePlugin(this);
        }
    }

    private void initDataServices() {
        var locationDao = new SqliteLocationDao(dataSource);
        locationService = new LocationService(locationDao);
        locationService.cacheLocations(getServer());

        var homeDao = new SqliteHomeDao(dataSource);
        homeService = new HomeService(homeDao);
        smpPlayerService = new SmpPlayerService(this);
    }

    private void initCommandManager() {
        try {
            this.commandManager = new PaperCommandManager<>(
                    this,
                    CommandExecutionCoordinator.simpleCoordinator(),
                    Function.identity(),
                    Function.identity());
        } catch (Exception e) {
            this.getLogger().log(Level.SEVERE, "Failed to initialize command manager", e);
            this.getServer().getPluginManager().disablePlugin(this);
        }
    }

    public PluginHookService pluginHookService() {
        return pluginHookService;
    }
    public Configuration configuration() {
        return configuration;
    }

    public Messenger messenger() {
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

    public TeleportQueue teleportQueue() {
        return teleportQueue;
    }

    public ConfigurationLoader configurationLoader() {
        return configurationLoader;
    }
}
