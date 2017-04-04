package de.antidrowsinessalarm.eventproducer;

import com.google.common.base.MoreObjects;

import org.joda.time.Duration;

public class DrowsyEventDetectorConfig {

    private final float eyeOpenProbabilityThreshold;
    private final DrowsyEventProducer.Config config;
    private final Duration slowEyelidClosureMinDuration;
    private final Duration timeWindow;

    DrowsyEventDetectorConfig(DrowsyEventDetectorConfigBuilder builder) {
        this.eyeOpenProbabilityThreshold = builder.getEyeOpenProbabilityThreshold();
        this.config = builder.getConfig();
        this.slowEyelidClosureMinDuration = builder.getSlowEyelidClosureMinDuration();
        this.timeWindow = builder.getTimeWindow();
    }

    public static DrowsyEventDetectorConfigBuilder builder() {
        return new DrowsyEventDetectorConfigBuilder();
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

}
