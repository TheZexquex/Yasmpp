package dev.thezexquex.yasmpp.modules.signedit;

import dev.thezexquex.yasmpp.data.entity.SmpPlayer;
import io.papermc.paper.event.player.PlayerOpenSignEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import java.util.ArrayList;
import java.util.List;

public class SignEditListener implements Listener {

    @EventHandler
    public void onSignEdit(SignChangeEvent event) {
        var lines = event.lines();
        for (Component line : lines) {
            event.line(lines.indexOf(line), getStyledComponent(line));
        }
    }

    public Component getStyledComponent(Component component) {
        return MiniMessage.miniMessage().deserialize(PlainTextComponentSerializer.plainText().serialize(component));
    }

    public Component getRawComponent(Component component) {
        return PlainTextComponentSerializer.plainText().deserialize(MiniMessage.miniMessage().serialize(component));
    }

    @EventHandler
    public void onSignOpen(PlayerOpenSignEvent event) {
        var lines = event.getSign().getSide(event.getSide()).lines();
        for (Component line : lines) {
            System.out.println(MiniMessage.miniMessage().serialize(line));
            System.out.println(getRawComponent(line));
            event.getSign().getSide(event.getSide()).line(1, Component.text("<red>Test"));
        }
        event.getSign().update();
    }
}
