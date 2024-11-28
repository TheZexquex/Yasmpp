package dev.thezexquex.yasmpp.configuration.settings;

import de.unknowncity.astralib.libs.com.fasterxml.jackson.annotation.JsonProperty;

public final class ChatSettings {
    @JsonProperty
    private boolean handleChat = true;

    public ChatSettings() {

    }

    public boolean handleChat() {
        return handleChat;
    }

    public void setHandleChat(boolean handleChat) {
        this.handleChat = handleChat;
    }
}
