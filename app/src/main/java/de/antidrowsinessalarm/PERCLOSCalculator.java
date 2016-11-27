package de.antidrowsinessalarm;

import java.util.List;

import de.antidrowsinessalarm.event.SlowEyelidClosureEvent;

public class PERCLOSCalculator {

    private final long timeWindowMillis;

    public PERCLOSCalculator(final long timeWindowMillis) {
        this.timeWindowMillis = timeWindowMillis;
    }

    public double calculatePERCLOS(final List<SlowEyelidClosureEvent> events, final long timewindowEndMillis) {
        return (double) this.getSumDurationsMillis(events, timewindowEndMillis) / (double) this.timeWindowMillis;
    }

    private long getSumDurationsMillis(final List<SlowEyelidClosureEvent> events, final long timewindowEndMillis) {
        long sum = 0;
        for(SlowEyelidClosureEvent event : events) {
            sum += this.getIntersectionWithTimewindow(event, timewindowEndMillis);
        }
        return sum;
    }

    private long getIntersectionWithTimewindow(final SlowEyelidClosureEvent event, final long timewindowEndMillis) {
        // TODO: use Joda-Time Interval [startMillis, endMillis] or guava Range
        final long timewindowStartMillis = timewindowEndMillis - this.timeWindowMillis;

        final long eventStartMillis = event.getTimestampMillis();
        final long eventEndMillis = this.getEndMillis(event);

        boolean hasNoIntersection = eventEndMillis < timewindowStartMillis || eventStartMillis > timewindowEndMillis;
        if(hasNoIntersection) {
            return 0;
        }

        final long intersectionStartMillis = Math.max(eventStartMillis, timewindowStartMillis);
        final long intersectionEndMillis = Math.min(eventEndMillis, timewindowEndMillis);

        return intersectionEndMillis - intersectionStartMillis;
    }

    private long getEndMillis(final SlowEyelidClosureEvent event) {
        return event.getTimestampMillis() + event.getDurationMillis();
    }
}
