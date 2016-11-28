package de.antidrowsinessalarm.eventproducer;

import org.joda.time.Duration;

public class ConfigFactory {

    public static DrowsyEventProducer.Config createDefaultConfig() {
        return DrowsyEventProducer.Config.builder()
                .setDrowsyThreshold(0.15)
                .setLikelyDrowsyThreshold(0.08)
                .build();
    }

    // TODO: make durationMillis configurable from 300 to 500 milliseconds
    public static Duration getDefaultSlowEyelidClosureMinDuration() {
        return new Duration(500);
    }

    public static float getDefaultEyeOpenProbabilityThreshold() {
        return 0.5f;
    }

    public static Duration getDefaultTimeWindow() {
        return new Duration(15000);
    }
}
