package dev.thezexquex.yasmpp.commands.ontime;

import de.unknowncity.astralib.common.message.lang.Language;
import de.unknowncity.astralib.paper.api.message.PaperMessenger;
import dev.thezexquex.yasmpp.data.plan.PlanUser;
import dev.thezexquex.yasmpp.util.MapUtil;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.NodePath;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import static dev.thezexquex.yasmpp.util.DurationFormatter.formatDuration;
import static dev.thezexquex.yasmpp.util.DurationFormatter.formatLastLogoutDuration;

public class PlayTimePrinter {

    public static void printPlayTime(CommandSender receiver, PlanUser planUser, String serverName, boolean online, PaperMessenger messenger) {
        var firstSession = planUser.firstSessionServer(serverName == null ? "" : serverName);
        var onTimeCurrentSession = planUser.sessionPlayTime();

        var firstJoinedServer =  LocalDateTime.MIN;
        if (firstSession.isPresent()) {
            firstJoinedServer = firstSession.get().sessionStartTime();
        }

        if (serverName == null) {
            firstJoinedServer = planUser.globalFirstLogin();
        }

        var lastSession = serverName != null ? planUser.lastSessionServer(serverName) : planUser.lastSessionGlobal();

        var firstJoinedAgo = Duration.between(firstJoinedServer, LocalDateTime.now()).toDays();

        var onTimeDay = serverName != null ? planUser.getOnTimeDayServer(serverName) : planUser.getOnTimeDay();
        var onTimeWeek = serverName != null ? planUser.getOnTimeWeekServer(serverName) : planUser.getOnTimeWeek();
        var onTimeMonth = serverName != null ? planUser.getOnTimeMonthServer(serverName) : planUser.getOnTimeMonth();
        var onTimeYear = serverName != null ? planUser.getOnTimeYearServer(serverName) : planUser.getOnTimeYear();
        var onTimeAll = serverName != null ? planUser.getOnTimeTotalServer(serverName) : planUser.getOnTimeTotal();
        var year = LocalDate.now().getYear();
        var empty = messenger.getString(Language.GERMAN, NodePath.path("command", "ontime", "empty"));

        var node = NodePath.path("command", "ontime", "global");

        String time = "Online";

        if (!online) {
            node = NodePath.path("command", "ontime", "global-offline");
            time = lastSession.isEmpty() ? empty : formatLastLogoutDuration(Duration.between(lastSession.get().sessionEndTime(), LocalDateTime.now()), empty);
        }

        messenger.sendMessage(
                receiver,
                node,
                Placeholder.parsed("server", serverName != null ? serverName : "Global"),
                Placeholder.parsed("player", planUser.name()),
                Placeholder.parsed("first_joined", firstJoinedServer.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))),
                Placeholder.parsed("first_joined_ago", String.valueOf(firstJoinedAgo)),
                Placeholder.parsed("ontime_session", online ? formatDuration(onTimeCurrentSession, empty) : time),
                Placeholder.parsed("ontime_day", formatDuration(onTimeDay, empty)),
                Placeholder.parsed("ontime_week", formatDuration(onTimeWeek, empty)),
                Placeholder.parsed("ontime_month", formatDuration(onTimeMonth, empty)),
                Placeholder.parsed("ontime_year", formatDuration(onTimeYear, empty)),
                Placeholder.parsed("ontime_all", formatDuration(onTimeAll, empty)),
                Placeholder.parsed("year", String.valueOf(year))

        );
    }
    
    public static void printPlayTimeTop(Player receiver, HashMap<String, Duration> playTimeForPlayers, int page, PaperMessenger messenger) {
        
        var ontimeSorted = MapUtil.sortByValue(playTimeForPlayers);
        var values = ontimeSorted.values().stream().toList();
        var keys = ontimeSorted.keySet().stream().toList();

        var lowerBoundInclusive = (page - 1) * 10 + 1;
        var upperBoundInclusive = Math.min(lowerBoundInclusive + 9, values.size() - 1);

        var maxPage = values.size() / 10 + 1;

        var receiverIndex = keys.indexOf(receiver.getName());

        if (page > maxPage) {
            messenger.sendMessage(receiver, NodePath.path("command", "ontimetop", "max-pages-reached"));
            return;
        }

        messenger.sendMessage(receiver, NodePath.path("command", "ontimetop", "header"));

        if (receiverIndex + 1 < lowerBoundInclusive) {
            messenger.sendMessage(receiver, NodePath.path("command", "ontimetop", "list-self"),
                    Placeholder.parsed("player", receiver.getName()),
                    Placeholder.parsed("count", String.valueOf(receiverIndex + 1)),
                    Placeholder.parsed("ontime", formatDuration(values.get(receiverIndex), "N/A")));
        }

        for (int i = lowerBoundInclusive - 1; i <= upperBoundInclusive; i++) {
            var playerName = keys.get(i);

            if (receiver.getName().equals(playerName)) {
                messenger.sendMessage(receiver, NodePath.path("command", "ontimetop", "list-self"),
                        Placeholder.parsed("count", String.valueOf(i + 1)),
                        Placeholder.parsed("player", playerName),
                        Placeholder.parsed("ontime", formatDuration(values.get(i), "N/A"))
                );
            } else {
                messenger.sendMessage(receiver, NodePath.path("command", "ontimetop", "list"),
                        Placeholder.parsed("count", String.valueOf(i + 1)),
                        Placeholder.parsed("player", playerName),
                        Placeholder.parsed("ontime", formatDuration(values.get(i), "N/A"))
                );
            }
        }

        if (receiverIndex + 1 > page * 10) {
            messenger.sendMessage(receiver, NodePath.path("command", "ontimetop", "list-self"),
                    Placeholder.parsed("count", String.valueOf(receiverIndex + 1)),
                    Placeholder.parsed("player", receiver.getName()),
                    Placeholder.parsed("ontime", formatDuration(values.get(receiverIndex), "N/A"))
            );
        }

        if (page == 1) {
            if (page < maxPage) {
                messenger.sendMessage(
                        receiver,
                        NodePath.path("command", "ontimetop", "footer-no-prev"),
                        Placeholder.parsed("next", String.valueOf(page + 1)),
                        Placeholder.parsed("current-page", String.valueOf(page)),
                        Placeholder.parsed("max-page", String.valueOf(maxPage))
                );
            } else {
                messenger.sendMessage(
                        receiver,
                        NodePath.path("command", "ontimetop", "footer-no-prev-no-next"),
                        Placeholder.parsed("next", String.valueOf(page + 1)),
                        Placeholder.parsed("current-page", String.valueOf(page)),
                        Placeholder.parsed("max-page", String.valueOf(maxPage))
                );
            }
        } else if (page + 1 > maxPage) {
            messenger.sendMessage(
                    receiver,
                    NodePath.path("command", "ontimetop", "footer-no-next"),
                    Placeholder.parsed("previous", String.valueOf(page - 1)),
                    Placeholder.parsed("current-page", String.valueOf(page)),
                    Placeholder.parsed("max-page", String.valueOf(maxPage))
            );
        } else {
            messenger.sendMessage(
                    receiver,
                    NodePath.path("command", "ontimetop", "footer"),
                    Placeholder.parsed("previous", String.valueOf(page - 1)),
                    Placeholder.parsed("next", String.valueOf(page + 1)),
                    Placeholder.parsed("current-page", String.valueOf(page)),
                    Placeholder.parsed("max-page", String.valueOf(maxPage))
            );
        }
    }
}
