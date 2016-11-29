package de.antidrowsinessalarm.eventproducer;

import org.joda.time.Duration;

public class DefaultConfigFactory {

    public static DrowsyEventProducer.Config createConfig() {
        return DrowsyEventProducer.Config
                .builder()
                .withDrowsyThreshold(0.15)
                .withLikelyDrowsyThreshold(0.08)
                .build();
    }

    // TODO: make durationMillis configurable from 300 to 500 milliseconds
    public static Duration getSlowEyelidClosureMinDuration() {
        return new Duration(500);
    }

    public static float getEyeOpenProbabilityThreshold() {
        return 0.5f;
    }

    public static Duration getTimeWindow() {
        return new Duration(15000);
    }
}
