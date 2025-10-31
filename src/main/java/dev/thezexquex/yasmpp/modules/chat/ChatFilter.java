package dev.thezexquex.yasmpp.modules.chat;

import dev.thezexquex.yasmpp.Permissions;
import dev.thezexquex.yasmpp.YasmpPlugin;
import dev.thezexquex.yasmpp.configuration.settings.ChatFilterRule;
import org.bukkit.entity.Player;

import java.util.List;

public class ChatFilter {
    private final YasmpPlugin plugin;

    public ChatFilter(YasmpPlugin plugin) {
        this.plugin = plugin;
    }

    public class ChatFilterResult {
        private final Outcome outcome;
        private final String message;
        public ChatFilterResult(Outcome outcome, String message) {
            this.outcome = outcome;
            this.message = message;
        }
        public enum Outcome {
            PASS, BLOCK
        }

        public Outcome outcome() {
            return outcome;
        }

        public String message() {
            return message;
        }
    }

    public ChatFilterResult applyFilter(Player player, String input) {
        var result = input;
        for (ChatFilterRule chatFilterRule : plugin.configuration().chat().filter()) {
            if (violatesRule(input, chatFilterRule) && !player.hasPermission(Permissions.CHAT_VIOLATION_BYPASS)) {
                var ruleResult = chatFilterRule.method().apply(result, chatFilterRule.pattern());
                if (ruleResult == null) {
                    return new ChatFilterResult(ChatFilterResult.Outcome.BLOCK, input);
                }

                result = ruleResult;
            }
        }

        return new ChatFilterResult(ChatFilterResult.Outcome.PASS, result);
    }

    public boolean violatesRule(String input, ChatFilterRule chatFilterRule) {
        if (chatFilterRule.type() == ChatFilterRule.Type.FORBIDDEN && chatFilterRule.pattern().matcher(input).find()) {
            return true;
        }

        return chatFilterRule.type() == ChatFilterRule.Type.REQUIRED && !chatFilterRule.pattern().matcher(input).find();
    }
}