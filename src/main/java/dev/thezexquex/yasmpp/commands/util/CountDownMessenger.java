package dev.thezexquex.yasmpp.commands.util;

import com.google.common.collect.Streams;
import de.unknowncity.astralib.paper.api.message.PaperMessenger;
import dev.thezexquex.yasmpp.configuration.settings.CountDownEntry;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.NodePath;

import java.time.Duration;
import java.util.Arrays;

public class CountDownMessenger {

    public static void broadcastCountDown(
            PaperMessenger messenger,
            CountDownEntry countDownEntry,
            NodePath messagePath,
            NodePath titlePath,
            NodePath subtitlePath,
            TagResolver... tagResolvers
    ) {;
        if (countDownEntry.useChat()) {
            messenger.broadcastMessage(
                    messagePath,
                    Streams.concat(
                            Arrays.stream(DurationPlaceholders.forDuration(Duration.ofSeconds(countDownEntry.second()))),
                            Arrays.stream(tagResolvers)
                    ).toArray(TagResolver[]::new)
            );
        }

        if (countDownEntry.useTitle()) {
            messenger.broadcastTitle(
                    titlePath,
                    subtitlePath,
                    Title.Times.times(Duration.ZERO, Duration.ofSeconds(1), Duration.ZERO),
                    Streams.concat(
                            Arrays.stream(DurationPlaceholders.forDuration(Duration.ofSeconds(countDownEntry.second()))),
                            Arrays.stream(tagResolvers)
                    ).toArray(TagResolver[]::new)
            );
        }

        if (countDownEntry.useSound()) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.playSound(countDownEntry.sound());
            }
        }
    }

    public static void sendCountDown(
            Player player,
            PaperMessenger messenger,
            CountDownEntry countDownEntry,
            NodePath messagePath,
            NodePath titlePath,
            NodePath subtitlePath,
            TagResolver... tagResolvers
    ) {;
        if (countDownEntry.useChat()) {
            messenger.sendMessage(
                    player,
                    messagePath,
                    Streams.concat(
                            Arrays.stream(DurationPlaceholders.forDuration(Duration.ofSeconds(countDownEntry.second()))),
                            Arrays.stream(tagResolvers)
                    ).toArray(TagResolver[]::new)
            );
        }

        if (countDownEntry.useTitle()) {
            messenger.sendTitle(
                    player,
                    titlePath,
                    subtitlePath,
                    Title.Times.times(Duration.ZERO, Duration.ofSeconds(1), Duration.ZERO),
                    Streams.concat(
                            Arrays.stream(DurationPlaceholders.forDuration(Duration.ofSeconds(countDownEntry.second()))),
                            Arrays.stream(tagResolvers)
                    ).toArray(TagResolver[]::new)
            );
        }

        if (countDownEntry.useSound()) {
            player.playSound(countDownEntry.sound());
        }
    }
}
