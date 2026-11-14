package dev.thezexquex.yasmpp.configuration.settings;

import dev.thezexquex.yasmpp.modules.chat.method.BlockMethod;
import dev.thezexquex.yasmpp.modules.chat.method.ReplaceMethod;

import java.util.List;

public final class ChatSettings {
    private boolean handleChat = true;

    private List<ChatFilterRule> filter = List.of(
            new ChatFilterRule("[s,S].*[p,P].*[e,E,3].*[e,E,3].*[d,D].*[k,K].*[i,I].*[n,N].*[g,G].*?[s,S]", ChatFilterRule.Type.FORBIDDEN, new BlockMethod()),
            new ChatFilterRule("/(TheZexquex ist) .*/gmi", ChatFilterRule.Type.FORBIDDEN, new ReplaceMethod("$1 ein cooler Typ"))
    );

    public boolean handleChat() {
        return handleChat;
    }

    public List<ChatFilterRule> filter() {
        return filter;
    }
}
