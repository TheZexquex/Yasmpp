package dev.thezexquex.yasmpp.modules.sysinfo;

import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;

public enum SysInfoSection {
    OS,
    CPU,
    MEMORY,
    DISK,
    SERVER;

    public String commandName() {
        return name().toLowerCase(Locale.ROOT);
    }

    /**
     * The sections shown by {@code /sysinfo} without a subcommand. Server values are only part of the detailed view.
     */
    public static Set<SysInfoSection> overview(boolean detailed) {
        return detailed ? EnumSet.allOf(SysInfoSection.class) : EnumSet.of(OS, CPU, MEMORY, DISK);
    }
}
