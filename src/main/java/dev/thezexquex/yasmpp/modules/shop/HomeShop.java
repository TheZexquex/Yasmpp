package dev.thezexquex.yasmpp.modules.shop;

import org.bukkit.entity.Player;

public class HomeShop {

    public static int getCurrentMaxHomes(Player player) {
        var permissions = player.getEffectivePermissions();
        var permissionOpt = permissions.stream().filter(permissionAttachmentInfo -> permissionAttachmentInfo
                .getPermission().startsWith("yasmpp.homes.")).findFirst();

        var maxHomesString = permissionOpt.map(permissionAttachmentInfo -> permissionAttachmentInfo.getPermission().replace("yasmpp.homes.", "")).orElse("0");
        int maxHomes;
        try {
            maxHomes = Integer.parseInt(maxHomesString);
        } catch (NumberFormatException e) {
            maxHomes = -1;
        }
        return maxHomes;
    }

    public void addHome(Player player) {

    }

}