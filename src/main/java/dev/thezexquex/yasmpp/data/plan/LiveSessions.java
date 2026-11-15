package dev.thezexquex.yasmpp.data.plan;

import com.djrapitops.plan.query.QueryService;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reads the currently running sessions on this server from Plan's in-memory session cache.
 * Plan only persists a session when it ends, so these are missing from the database.
 */
public class LiveSessions {
    private final Logger logger;
    private AfkAccess afkAccess;

    public LiveSessions(Logger logger) {
        this.logger = logger;
    }

    public Optional<LiveSession> session(UUID uniqueId, Instant now) {
        var length = QueryService.getInstance().getCommonQueries().fetchCurrentSessionPlaytime(uniqueId);
        if (length <= 0) {
            return Optional.empty();
        }
        return Optional.of(new LiveSession(now.minusMillis(length), Duration.ofMillis(length), afkTime(uniqueId)));
    }

    /**
     * The AFK time of a running session is not part of Plan's public API, so it is read via reflection.
     * If that fails (e.g. after a Plan update) the AFK time of running sessions is treated as zero.
     */
    private Duration afkTime(UUID uniqueId) {
        if (afkAccess == null) {
            afkAccess = AfkAccess.create(logger);
        }
        if (afkAccess == AfkAccess.UNAVAILABLE) {
            return Duration.ZERO;
        }
        try {
            return afkAccess.afkTime(uniqueId);
        } catch (ReflectiveOperationException | RuntimeException e) {
            logger.log(Level.WARNING, "Could not read AFK time from Plan's session cache, AFK time of running sessions is ignored", e);
            afkAccess = AfkAccess.UNAVAILABLE;
            return Duration.ZERO;
        }
    }

    public record LiveSession(Instant start, Duration length, Duration afk) {
    }

    private record AfkAccess(Method getCachedSession, Method toFinishedSession, Method afkTime) {
        private static final AfkAccess UNAVAILABLE = new AfkAccess(null, null, null);

        static AfkAccess create(Logger logger) {
            try {
                var classLoader = QueryService.getInstance().getClass().getClassLoader();
                var sessionCache = Class.forName("com.djrapitops.plan.gathering.cache.SessionCache", true, classLoader);
                var activeSession = Class.forName("com.djrapitops.plan.gathering.domain.ActiveSession", true, classLoader);
                var finishedSession = Class.forName("com.djrapitops.plan.gathering.domain.FinishedSession", true, classLoader);
                return new AfkAccess(
                        sessionCache.getMethod("getCachedSession", UUID.class),
                        activeSession.getMethod("toFinishedSessionFromStillActive"),
                        finishedSession.getMethod("afkTime")
                );
            } catch (ReflectiveOperationException | RuntimeException e) {
                logger.log(Level.WARNING, "Could not access Plan's session cache, AFK time of running sessions is ignored", e);
                return UNAVAILABLE;
            }
        }

        Duration afkTime(UUID uniqueId) throws ReflectiveOperationException {
            var activeSession = (Optional<?>) getCachedSession.invoke(null, uniqueId);
            if (activeSession.isEmpty()) {
                return Duration.ZERO;
            }
            var finishedSession = toFinishedSession.invoke(activeSession.get());
            return Duration.ofMillis((long) afkTime.invoke(finishedSession));
        }
    }
}
