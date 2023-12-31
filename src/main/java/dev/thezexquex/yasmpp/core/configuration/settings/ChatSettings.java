package dev.thezexquex.yasmpp.core.configuration.settings;

public final class ChatSettings {
    private boolean handleChat;

    public ChatSettings(
            boolean handleChat
    ) {
        this.handleChat = handleChat;
    }

    public boolean handleChat() {
        return handleChat;
    }

    public void setHandleChat(boolean handleChat) {
        this.handleChat = handleChat;
    }
}
