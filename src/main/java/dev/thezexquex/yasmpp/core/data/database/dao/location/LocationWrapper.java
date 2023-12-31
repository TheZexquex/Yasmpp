package dev.thezexquex.yasmpp.core.data.database.dao.location;

import dev.thezexquex.yasmpp.core.data.database.dao.location.LocationContainer;

public record LocationWrapper(
        String name,
        LocationContainer locationContainer
) {}
