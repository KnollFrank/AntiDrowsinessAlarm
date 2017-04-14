package de.drowsydriveralarm.eventproducer;

import com.google.common.base.MoreObjects;

import org.joda.time.Duration;

public class DrowsyEventDetectorConfig implements IDrowsyEventDetectorConfig {

    private final float eyeOpenProbabilityThreshold;
    private final DrowsyEventProducer.Config config;
    private final Duration slowEyelidClosureMinDuration;
    private final Duration timeWindow;

    DrowsyEventDetectorConfig(final DrowsyEventDetectorConfigBuilder builder) {
        this.eyeOpenProbabilityThreshold = builder.getEyeOpenProbabilityThreshold();
        this.config = builder.getConfig();
        this.slowEyelidClosureMinDuration = builder.getSlowEyelidClosureMinDuration();
        this.timeWindow = builder.getTimeWindow();
    }

    public static DrowsyEventDetectorConfigBuilder builder() {
        return new DrowsyEventDetectorConfigBuilder();
    }

    @Override
    public float getEyeOpenProbabilityThreshold() {
        return this.eyeOpenProbabilityThreshold;
    }

    @Override
    public DrowsyEventProducer.Config getConfig() {
        return this.config;
    }

    @Override
    public Duration getSlowEyelidClosureMinDuration() {
        return this.slowEyelidClosureMinDuration;
    }

    @Override
    public Duration getTimeWindow() {
        return this.timeWindow;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("eyeOpenProbabilityThreshold", this.eyeOpenProbabilityThreshold)
                .add("config", this.config)
                .add("slowEyelidClosureMinDuration", this.slowEyelidClosureMinDuration)
                .add("timeWindow", this.timeWindow)
                .toString();
    }

}
