package de.drowsydriveralarm.eventproducer;

import android.content.SharedPreferences;

import org.joda.time.Duration;

public class TestingDrowsyEventDetectorConfig implements IDrowsyEventDetectorConfig {

    private final SharedPreferences sharedPreferences;

    // TODO: use Dagger for DI
    public TestingDrowsyEventDetectorConfig(final SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    @Override
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

    @Override
    public Duration getSlowEyelidClosureMinDuration() {
        return new Duration(this.sharedPreferences.getInt("slowEyelidClosureMinDuration", 500));
    }

    @Override
    public float getEyeOpenProbabilityThreshold() {
        return this.sharedPreferences.getInt("eyeOpenProbabilityThreshold", 50) / 100.0f;
    }

    @Override
    public Duration getTimeWindow() {
        return new Duration(Long.valueOf(this.sharedPreferences.getString("timeWindow", "15000")));
    }
}
