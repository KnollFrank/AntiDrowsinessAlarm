package de.antidrowsinessalarm.eventproducer;

import android.support.annotation.NonNull;

import com.google.common.eventbus.EventBus;

import org.joda.time.Instant;

import de.antidrowsinessalarm.PERCLOSCalculator;
import de.antidrowsinessalarm.event.AwakeEvent;
import de.antidrowsinessalarm.event.DrowsyEvent;
import de.antidrowsinessalarm.event.LikelyDrowsyEvent;

public class DrowsyEventProducer extends EventProducer {

    private final Config config;
    private final SlowEyelidClosureEventsProvider slowEyelidClosureEventsProvider;

    public DrowsyEventProducer(final Config config, final EventBus eventBus, final SlowEyelidClosureEventsProvider slowEyelidClosureEventsProvider) {
        super(eventBus);
        this.config = config;
        this.slowEyelidClosureEventsProvider = slowEyelidClosureEventsProvider;
    }

    public void maybeProduceDrowsyEvent(final Instant now) {
        double perclos = this.getPerclos(now);
        if (perclos >= this.config.getDrowsyThreshold()) {
            this.postEvent(new DrowsyEvent(now, perclos));
        } else if (perclos >= this.config.getLikelyDrowsyThreshold()) {
            this.postEvent(new LikelyDrowsyEvent(now, perclos));
        } else {
            this.postEvent(new AwakeEvent(now, perclos));
        }
    }

    private double getPerclos(final Instant now) {
        return this.getPERCLOSCalculator().calculatePERCLOS(this.slowEyelidClosureEventsProvider.getRecordedEventsPartlyWithinTimeWindow(now), now);
    }

    @NonNull
    private PERCLOSCalculator getPERCLOSCalculator() {
        return new PERCLOSCalculator(this.slowEyelidClosureEventsProvider.getTimeWindow());
    }

    public static class Config {

        private final double drowsyThreshold;
        private final double likelyDrowsyThreshold;

        private Config(final ConfigBuilder builder) {
            this.drowsyThreshold = builder.drowsyThreshold;
            this.likelyDrowsyThreshold = builder.likelyDrowsyThreshold;
        }

        public static ConfigBuilder builder() {
            return new ConfigBuilder();
        }

        public double getDrowsyThreshold() {
            return this.drowsyThreshold;
        }

        public double getLikelyDrowsyThreshold() {
            return this.likelyDrowsyThreshold;
        }

        public static class ConfigBuilder {

            private double drowsyThreshold;
            private double likelyDrowsyThreshold;

            private ConfigBuilder() {
            }

            public ConfigBuilder withDrowsyThreshold(final double drowsyThreshold) {
                this.drowsyThreshold = drowsyThreshold;
                return this;
            }

            public ConfigBuilder withLikelyDrowsyThreshold(final double likelyDrowsyThreshold) {
                this.likelyDrowsyThreshold = likelyDrowsyThreshold;
                return this;
            }

            public Config build() {
                return new Config(this);
            }
        }
    }
}
