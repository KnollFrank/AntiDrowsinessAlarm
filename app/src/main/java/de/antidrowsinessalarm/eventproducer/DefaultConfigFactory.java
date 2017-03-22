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
        return DrowsyEventProducer.Config
                .builder()
                .withDrowsyThreshold(this.getDrowsyThreshold())
                .withLikelyDrowsyThreshold(this.getLikelyDrowsyThreshold())
                .build();
    }

    // TODO: es muß immer gelten: likelyDrowsyThreshold <= drowsyThreshold, und das muß in den Preferencecs auch nur so einstelllbar sein.
    private Double getLikelyDrowsyThreshold() {
        return Double.valueOf(this.sharedPreferences.getString("likelyDrowsyThreshold", "0.08"));
    }

    private Double getDrowsyThreshold() {
        return Double.valueOf(this.sharedPreferences.getString("drowsyThreshold", "0.15"));
    }

    // TODO: make durationMillis configurable from 300 to 500 milliseconds
    public Duration getSlowEyelidClosureMinDuration() {
        return new Duration(Long.valueOf(this.sharedPreferences.getString("slowEyelidClosureMinDuration", "500")));
    }

    // TODO: constrain to 0 <= eyeOpenProbabilityThreshold <= 1
    public float getEyeOpenProbabilityThreshold() {
        return Float.valueOf(this.sharedPreferences.getString("eyeOpenProbabilityThreshold", "0.5"));
    }

    public Duration getTimeWindow() {
        return new Duration(Long.valueOf(this.sharedPreferences.getString("timeWindow", "15000")));
    }
}
