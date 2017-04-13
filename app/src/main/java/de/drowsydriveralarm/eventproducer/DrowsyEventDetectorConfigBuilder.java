package de.drowsydriveralarm.eventproducer;

import org.joda.time.Duration;

public class DrowsyEventDetectorConfigBuilder {

    private float eyeOpenProbabilityThreshold;
    private DrowsyEventProducer.Config config;
    private Duration slowEyelidClosureMinDuration;
    private Duration timeWindow;

    DrowsyEventDetectorConfigBuilder() {
    }

    public DrowsyEventDetectorConfigBuilder withEyeOpenProbabilityThreshold(final float eyeOpenProbabilityThreshold) {
        this.eyeOpenProbabilityThreshold = eyeOpenProbabilityThreshold;
        return this;
    }

    public DrowsyEventDetectorConfigBuilder withConfig(final DrowsyEventProducer.Config config) {
        this.config = config;
        return this;
    }

    public DrowsyEventDetectorConfigBuilder withSlowEyelidClosureMinDuration(final Duration slowEyelidClosureMinDuration) {
        this.slowEyelidClosureMinDuration = slowEyelidClosureMinDuration;
        return this;
    }

    public DrowsyEventDetectorConfigBuilder withTimeWindow(final Duration timeWindow) {
        this.timeWindow = timeWindow;
        return this;
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

    public DrowsyEventDetectorConfig build() {
        return new DrowsyEventDetectorConfig(this);
    }
}
