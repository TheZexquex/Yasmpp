package dev.thezexquex.yasmpp.modules.chat.method;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.regex.Pattern;

@JsonTypeName("replace")
public class ReplaceMethod implements FilterMethod {
    private final String type = "replace";
    private String replacement = "";

    public ReplaceMethod(String replacement) {
        this.replacement = replacement;
    }

    @Override
    public String apply(String input, Pattern pattern) {
        return pattern.matcher(input).replaceAll(replacement);
    }
}
