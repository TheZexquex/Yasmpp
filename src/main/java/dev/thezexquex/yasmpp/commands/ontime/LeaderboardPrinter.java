package dev.thezexquex.yasmpp.commands.ontime;

import dev.thezexquex.yasmpp.YasmpPlugin;
import dev.thezexquex.yasmpp.util.Page;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.spongepowered.configurate.NodePath;

import java.util.function.Function;
import java.util.function.IntFunction;

/**
 * Sends a page of a ranked list using the messages {@code command.<root>.header}, {@code entry}, {@code entry-self}
 * and the shared {@code pagination} messages.
 */
public class LeaderboardPrinter {
    private final YasmpPlugin plugin;

    public LeaderboardPrinter(YasmpPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * @param pageCommand builds the command that opens the given page, used for the footer buttons
     * @param entryTags   placeholders of one entry in addition to {@code <count>}
     */
    public <T> void send(CommandSender receiver, String root, Page<T> page, IntFunction<String> pageCommand,
                         Function<T, TagResolver> entryTags, TagResolver headerTags) {
        var messenger = plugin.messenger();

        messenger.sendMessage(receiver, NodePath.path("command", root, "header"), headerTags);
        page.selfAbove().ifPresent(entry -> sendEntry(receiver, root, entry, entryTags));
        page.entries().forEach(entry -> sendEntry(receiver, root, entry, entryTags));
        page.selfBelow().ifPresent(entry -> sendEntry(receiver, root, entry, entryTags));

        var language = plugin.language(receiver);
        var previous = page.hasPrevious()
                ? messenger.component(language, NodePath.path("pagination", "previous"), Placeholder.parsed("command", pageCommand.apply(page.number() - 1)))
                : messenger.component(language, NodePath.path("pagination", "previous-none"));
        var next = page.hasNext()
                ? messenger.component(language, NodePath.path("pagination", "next"), Placeholder.parsed("command", pageCommand.apply(page.number() + 1)))
                : messenger.component(language, NodePath.path("pagination", "next-none"));

        messenger.sendMessage(receiver, NodePath.path("pagination", "footer"),
                Placeholder.component("previous", previous),
                Placeholder.component("next", next),
                Placeholder.parsed("current-page", String.valueOf(page.number())),
                Placeholder.parsed("max-page", String.valueOf(page.maxPage()))
        );
    }

    private <T> void sendEntry(CommandSender receiver, String root, Page.Ranked<T> entry, Function<T, TagResolver> entryTags) {
        plugin.messenger().sendMessage(receiver, NodePath.path("command", root, entry.self() ? "entry-self" : "entry"),
                Placeholder.parsed("count", String.valueOf(entry.rank())),
                entryTags.apply(entry.value())
        );
    }
}
