package de.antidrowsinessalarm.eventproducer;

import android.content.SharedPreferences;

import org.joda.time.Duration;

public class DefaultConfigFactory {

    private final SharedPreferences sharedPreferences;

    // TODO: use Dagger for DI
    public DefaultConfigFactory(final SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public DrowsyEventProducer.Config getConfig() {
        final Double drowsyThreshold = Double.valueOf(sharedPreferences.getString("drowsyThreshold", "0.15"));
        return DrowsyEventProducer.Config
                .builder()
                .withDrowsyThreshold(drowsyThreshold)
                .withLikelyDrowsyThreshold(0.08)
                .build();
    }

    // TODO: make durationMillis configurable from 300 to 500 milliseconds
    public Duration getSlowEyelidClosureMinDuration() {
        return new Duration(500);
    }

    public float getEyeOpenProbabilityThreshold() {
        return 0.5f;
    }

    public Duration getTimeWindow() {
        return new Duration(15000);
    }
}
