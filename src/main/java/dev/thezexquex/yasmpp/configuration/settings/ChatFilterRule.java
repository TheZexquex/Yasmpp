package dev.thezexquex.yasmpp.configuration.settings;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.thezexquex.yasmpp.modules.chat.method.FilterMethod;

import java.util.regex.Pattern;

public class ChatFilterRule {
    private final Pattern pattern;
    private final Type type;
    private final FilterMethod method;

    @JsonCreator
    public ChatFilterRule(
            @JsonProperty("regex") String regex,
            @JsonProperty("type") Type type,
            @JsonProperty("method") FilterMethod method
    ) {
        this.pattern = Pattern.compile(regex);
        this.type = type;
        this.method = method;
    }

    public enum Type {
        REQUIRED, FORBIDDEN
    }

    public Pattern pattern() {
        return pattern;
    }

    public Type type() {
        return type;
    }

    public FilterMethod method() {
        return method;
    }
}
