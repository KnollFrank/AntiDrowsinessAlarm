package de.antidrowsinessalarm.eventproducer;

import com.google.common.base.MoreObjects;

import org.joda.time.Duration;

public class DrowsyEventDetectorConfig {

    private final float eyeOpenProbabilityThreshold;
    private final DrowsyEventProducer.Config config;
    private final Duration slowEyelidClosureMinDuration;
    private final Duration timeWindow;

    private DrowsyEventDetectorConfig(ConfigBuilder builder) {
        this.eyeOpenProbabilityThreshold = builder.eyeOpenProbabilityThreshold;
        this.config = builder.config;
        this.slowEyelidClosureMinDuration = builder.slowEyelidClosureMinDuration;
        this.timeWindow = builder.timeWindow;
    }

    public static ConfigBuilder builder() {
        return new ConfigBuilder();
    }

    public float getEyeOpenProbabilityThreshold() {
        return this.eyeOpenProbabilityThreshold;
    }

    public DrowsyEventProducer.Config getConfig() {
        return this.config;
    }

    public Duration getSlowEyelidClosureMinDuration() {
        return this.slowEyelidClosureMinDuration;
    }

    public Duration getTimeWindow() {
        return this.timeWindow;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("eyeOpenProbabilityThreshold", eyeOpenProbabilityThreshold)
                .add("config", config)
                .add("slowEyelidClosureMinDuration", slowEyelidClosureMinDuration)
                .add("timeWindow", timeWindow)
                .toString();
    }

    public static class ConfigBuilder {

        private float eyeOpenProbabilityThreshold;
        private DrowsyEventProducer.Config config;
        private Duration slowEyelidClosureMinDuration;
        private Duration timeWindow;

        private ConfigBuilder() {
        }

        public ConfigBuilder withEyeOpenProbabilityThreshold(final float eyeOpenProbabilityThreshold) {
            this.eyeOpenProbabilityThreshold = eyeOpenProbabilityThreshold;
            return this;
        }

        public ConfigBuilder withConfig(final DrowsyEventProducer.Config config) {
            this.config = config;
            return this;
        }

        public ConfigBuilder withSlowEyelidClosureMinDuration(final Duration slowEyelidClosureMinDuration) {
            this.slowEyelidClosureMinDuration = slowEyelidClosureMinDuration;
            return this;
        }

        public ConfigBuilder withTimeWindow(final Duration timeWindow) {
            this.timeWindow = timeWindow;
            return this;
        }

        public DrowsyEventDetectorConfig build() {
            return new DrowsyEventDetectorConfig(this);
        }
    }
}
