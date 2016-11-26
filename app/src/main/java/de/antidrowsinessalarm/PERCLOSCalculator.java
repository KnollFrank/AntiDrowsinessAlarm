package de.antidrowsinessalarm;

import java.util.List;

import de.antidrowsinessalarm.event.SlowEyelidClosureEvent;

public class PERCLOSCalculator {

    public double calculatePERCLOS(final List<SlowEyelidClosureEvent> events, final long timeWindowMillis) {
        return (double) this.getSumDurationsMillis(events) / (double) timeWindowMillis;
    }

    private long getSumDurationsMillis(final List<SlowEyelidClosureEvent> events) {
        long sum = 0;
        for(SlowEyelidClosureEvent event : events) {
            // TODO: getDurationMillis is not correct, we have to use the intersection of the duration and the time window
            sum += event.getDurationMillis();
        }
        return sum;
    }
}
