package dev.thezexquex.yasmpp.modules.chat.method;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.regex.Pattern;

@JsonTypeName("block")
public class BlockMethod implements FilterMethod {
    private final String type = "block";

    @Override
    public String apply(String input, Pattern pattern) {
        return null;
    }
}
