package dev.thezexquex.yasmpp.modules.chat.method;

import de.unknowncity.astralib.libs.com.fasterxml.jackson.annotation.JsonProperty;
import de.unknowncity.astralib.libs.com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.regex.Pattern;

@JsonTypeName("block")
public class BlockMethod implements FilterMethod {
    @JsonProperty
    private final String type = "block";

    public BlockMethod() {

    }

    @Override
    public String apply(String input, Pattern pattern) {
        return null;
    }
}
