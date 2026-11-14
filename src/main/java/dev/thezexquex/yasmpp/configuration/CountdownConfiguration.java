package dev.thezexquex.yasmpp.configuration;

import dev.thezexquex.yasmpp.configuration.settings.CountDownSettings;

public class CountdownConfiguration {

    private final CountDownSettings countdown = new CountDownSettings();

    public CountDownSettings countdown() {
        return countdown;
    }
}
