package dev.thezexquex.yasmpp.hooks;

import com.djrapitops.plan.query.QueryService;
import de.unknowncity.astralib.paper.api.hook.PaperPluginHook;
import dev.thezexquex.yasmpp.YasmpPlugin;
import dev.thezexquex.yasmpp.commands.ontime.LastLoginsCommand;
import dev.thezexquex.yasmpp.commands.ontime.OnTimeCommand;
import dev.thezexquex.yasmpp.commands.ontime.OnTimeTopCommand;
import dev.thezexquex.yasmpp.data.plan.PlanQueryService;

public class PlanHook extends PaperPluginHook {
    private PlanQueryService planQueryService;
    public PlanHook(YasmpPlugin plugin) {
        super("Plan", plugin);
    }

    @Override
    public void initialize() {
        planQueryService = new PlanQueryService(plugin.getLogger());
        new LastLoginsCommand((YasmpPlugin) plugin).apply(plugin.commandManager());
        new OnTimeCommand((YasmpPlugin) plugin).apply(plugin.commandManager());
        new OnTimeTopCommand((YasmpPlugin) plugin).apply(plugin.commandManager());

        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            planQueryService.refreshPlanCache(QueryService.getInstance());
        }, 0, ((YasmpPlugin) plugin).configuration().general().planDataRefreshInterval() * 20L);
    }

    public PlanQueryService planQueryService() {
        return planQueryService;
    }
}
