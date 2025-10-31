package dev.thezexquex.yasmpp.configuration.settings;

import de.unknowncity.astralib.libs.com.fasterxml.jackson.annotation.JsonProperty;
import dev.thezexquex.yasmpp.modules.chat.method.BlockMethod;
import dev.thezexquex.yasmpp.modules.chat.method.ReplaceMethod;

import java.util.List;

public final class ChatSettings {
    @JsonProperty
    private boolean handleChat = true;

    @JsonProperty
    private List<ChatFilterRule> filter = List.of(
            new ChatFilterRule("[s,S].*[p,P].*[e,E,3].*[e,E,3].*[d,D].*[k,K].*[i,I].*[n,N].*[g,G].*?[s,S]", ChatFilterRule.Type.FORBIDDEN, new BlockMethod()),
            new ChatFilterRule("/(TheZexquex ist) .*/gmi", ChatFilterRule.Type.FORBIDDEN, new ReplaceMethod("$1 ein cooler Typ"))
    );

    public ChatSettings() {

    }

    public boolean handleChat() {
        return handleChat;
    }

    public List<ChatFilterRule> filter() {
        return filter;
    }
}
