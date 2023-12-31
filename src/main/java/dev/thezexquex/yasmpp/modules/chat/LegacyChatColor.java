package dev.thezexquex.yasmpp.modules.chat;

import org.bukkit.ChatColor;

import java.util.regex.Pattern;

public class LegacyChatColor {

    public static String translateToMiniMessage(Character legacyChar, String textToTranslate) {
        String textToReturn = replaceHexFormat(textToTranslate);
        var charArray = textToReturn.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            if (i == 0) {
                continue;
            }
            if (charArray[i-1] == legacyChar) {
                var chatColor = ChatColor.getByChar(charArray[i]);
                String tag = "";
                if (chatColor == null)
                    continue;
                switch (chatColor) {
                    case BOLD -> tag = "b";
                    case MAGIC -> tag = "obf";
                    case STRIKETHROUGH -> tag = "st";
                    case ITALIC -> tag = "i";
                    case UNDERLINE -> tag = "u";
                    default -> tag = chatColor.name().toLowerCase();
                }
                var finalTag = "<" + tag + ">";
                textToReturn = textToReturn.replace("" + legacyChar + charArray[i], finalTag);
            }
        }
        return textToReturn;
    }

    public static String replaceHexFormat(String input) {
        var output = input;
        var oldHexFormat = "&x&.&.&.&.&.&.";
        var matcher = Pattern.compile(oldHexFormat).matcher(output);
        String newHexFormat;
        while (matcher.find()) {
            newHexFormat = "<color:#......>";
            var currentOldHexFormat = matcher.group();
            var hexChars = currentOldHexFormat.replace("&", "").substring(1);
            newHexFormat = newHexFormat.replace("......", hexChars);
            output = output.replace(currentOldHexFormat, newHexFormat);
        }
        return output;
    }
}
