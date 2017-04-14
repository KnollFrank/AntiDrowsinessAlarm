package de.drowsydriveralarm.eventproducer;

import org.joda.time.Duration;

public interface IDrowsyEventDetectorConfig {

    float getEyeOpenProbabilityThreshold();

    DrowsyEventProducer.Config getConfig();

    Duration getSlowEyelidClosureMinDuration();

    Duration getTimeWindow();
}
