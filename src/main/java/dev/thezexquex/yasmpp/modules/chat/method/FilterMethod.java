package dev.thezexquex.yasmpp.modules.chat.method;

import de.unknowncity.astralib.libs.com.fasterxml.jackson.annotation.JsonSubTypes;
import de.unknowncity.astralib.libs.com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.regex.Pattern;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BlockMethod.class, name = "block"),
        @JsonSubTypes.Type(value = ReplaceMethod.class, name = "replace")
})
public interface FilterMethod {

    String apply(String input, Pattern pattern);
}
