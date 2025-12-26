package dev.thezexquex.yasmpp.util.timer;

import de.unknowncity.astralib.common.timer.Timer;
import de.unknowncity.astralib.common.timer.aborttrigger.AbortTrigger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class BukkitCountdown extends Timer {
    private JavaPlugin plugin;
    private Optional<Runnable> runOnAbort;

    public BukkitCountdown(
            TimeUnit timeUnit,
            Optional<Runnable> runOnFinish,
            Optional<Runnable> runOnAbort,
            Optional<Consumer<Duration>> runOnStep,
            Set<AbortTrigger> abortTriggers,
            JavaPlugin plugin
    ) {
        super(timeUnit, runOnFinish, runOnStep, abortTriggers);
        this.plugin = plugin;
        this.runOnAbort = runOnAbort;
    }

    public static class Builder {
        protected Optional<Runnable> runOnFinish = Optional.empty();
        protected Optional<Runnable> runOnAbort = Optional.empty();
        protected Optional<Consumer<Duration>> runOnStep = Optional.empty();
        protected TimeUnit timeUnit = TimeUnit.SECONDS;
        protected Set<AbortTrigger> abortTriggers = new HashSet<>();
        protected JavaPlugin plugin;

        public Builder(JavaPlugin plugin) {
            this.plugin = plugin;
        }

        public Builder withRunOnFinish(Runnable runOnFinish) {
            this.runOnFinish = Optional.of(runOnFinish);
            return this;
        }

        public Builder withRunOnStep(Consumer<Duration> runOnStep) {
            this.runOnStep = Optional.of(runOnStep);
            return this;
        }

        public Builder withRunOnAbort(Runnable runOnAbort) {
            this.runOnAbort = Optional.of(runOnAbort);
            return this;
        }

        public Builder withTimeUnit(TimeUnit timeUnit) {
            this.timeUnit = timeUnit;
            return this;
        }

        public Builder withAbortTriggers(AbortTrigger... abortTriggers) {
            this.abortTriggers.addAll(Arrays.asList(abortTriggers));
            return this;
        }

        public BukkitCountdown build() {
            return new BukkitCountdown(timeUnit, runOnFinish, runOnAbort, runOnStep , abortTriggers, plugin);
        }
    }

    public static BukkitCountdown.Builder builder(JavaPlugin plugin) {
        return new BukkitCountdown.Builder(plugin);
    }

    public void start(int timeSpan) {
        start(Long.valueOf(timeSpan));
    }

    public void start(long timeSpan) {
        running = true;
        try {
            step(timeSpan);
        } catch (Throwable t) {
            running = false;
            runOnAbort.ifPresent(Runnable::run);
            plugin.getLogger().warning("Failed to start countdown: " + t.getMessage());
        }
    }

    private void step(long timeSpan) {
        if (!running) return;

        for (AbortTrigger abortTrigger : abortTriggers) {
            if (abortTrigger.checkForPotentialTrigger()) {
                running = false;
                runOnAbort.ifPresent(Runnable::run);
                return;
            }
        }

        if (timeSpan <= 0) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                runOnFinish.ifPresent(Runnable::run);
            }, 1);
            running = false;
            return;
        }

        runOnStep.ifPresent(consumer ->
                consumer.accept(Duration.of(timeSpan, timeUnit.toChronoUnit()))
        );

        long delayTicks = Math.max(1L, timeUnit.toMillis(1) / 50);

        Bukkit.getScheduler().runTaskLater(
                plugin,
                () -> step(paused ? timeSpan : timeSpan - 1),
                delayTicks
        );
    }
}
