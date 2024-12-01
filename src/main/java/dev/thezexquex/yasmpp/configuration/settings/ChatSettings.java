package dev.thezexquex.yasmpp.configuration.settings;

import de.unknowncity.astralib.libs.com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public final class ChatSettings {
    @JsonProperty
    private boolean handleChat = true;

    @JsonProperty
    private List<String> forbiddenWords = List.of("[s,S].*[p,P].*[e,E,3].*[e,E,3].*[d,D].*[k,K].*[i,I].*[n,N].*[g,G].*?[s,S]");

    public ChatSettings() {

    }

    public boolean handleChat() {
        return handleChat;
    }

    public void setHandleChat(boolean handleChat) {
        this.handleChat = handleChat;
    }

    public List<String> forbiddenWords() {
        return forbiddenWords;
    }

    public void forbiddenWords(List<String> forbiddenWords) {
        this.forbiddenWords = forbiddenWords;
    }
}
