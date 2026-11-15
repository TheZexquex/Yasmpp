package dev.thezexquex.yasmpp.hooks;

import com.djrapitops.plan.query.QueryService;
import de.unknowncity.astralib.paper.api.hook.PaperPluginHook;
import dev.thezexquex.yasmpp.YasmpPlugin;
import dev.thezexquex.yasmpp.commands.ontime.LastLoginsCommand;
import dev.thezexquex.yasmpp.commands.ontime.OnTimeCommand;
import dev.thezexquex.yasmpp.commands.ontime.OnTimeTopCommand;
import dev.thezexquex.yasmpp.data.plan.LiveSessions;
import dev.thezexquex.yasmpp.data.plan.PlanQueryService;
import dev.thezexquex.yasmpp.data.plan.PlanSessionRepository;
import dev.thezexquex.yasmpp.data.plan.PlaytimeService;
import dev.thezexquex.yasmpp.modules.playtime.PlaytimeListener;

import java.time.ZoneId;

public class PlanHook extends PaperPluginHook {
    private PlanQueryService planQueryService;
    private PlaytimeService playtimeService;
    public PlanHook(YasmpPlugin plugin) {
        super("Plan", plugin);
    }

    @Override
    public void initialize() {
        var yasmpPlugin = (YasmpPlugin) plugin;
        var zone = ZoneId.systemDefault();
        playtimeService = new PlaytimeService(
                new PlanSessionRepository(zone),
                new LiveSessions(plugin.getLogger()),
                plugin.getServer(),
                runnable -> plugin.getServer().getScheduler().runTask(plugin, runnable),
                zone,
                plugin.getLogger()
        );
        plugin.getServer().getPluginManager().registerEvents(new PlaytimeListener(yasmpPlugin, playtimeService), plugin);
        // Warms up the leaderboard cache and the name suggestions
        playtimeService.leaderboard();

        planQueryService = new PlanQueryService(plugin.getLogger());
        new LastLoginsCommand(yasmpPlugin).apply(plugin.commandManager());
        new OnTimeCommand(yasmpPlugin).apply(plugin.commandManager());
        new OnTimeTopCommand(yasmpPlugin).apply(plugin.commandManager());

        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            planQueryService.refreshPlanCache(QueryService.getInstance());
        }, 0, yasmpPlugin.configuration().main().general().planDataRefreshInterval() * 20L);
    }

    public PlanQueryService planQueryService() {
        return planQueryService;
    }

    public PlaytimeService playtimeService() {
        return playtimeService;
    }
}
