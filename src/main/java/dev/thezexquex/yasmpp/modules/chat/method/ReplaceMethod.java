package dev.thezexquex.yasmpp.modules.chat.method;

import de.unknowncity.astralib.libs.com.fasterxml.jackson.annotation.JsonProperty;
import de.unknowncity.astralib.libs.com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.regex.Pattern;

@JsonTypeName("replace")
public class ReplaceMethod implements FilterMethod {
    @JsonProperty
    private final String type = "replace";
    @JsonProperty("replacement")
    private String replacement = "";

    public ReplaceMethod() {

    }

    public ReplaceMethod(String replacement) {
        this.replacement = replacement;
    }

    @Override
    public String apply(String input, Pattern pattern) {
        return pattern.matcher(input).replaceAll(replacement);
    }
}
