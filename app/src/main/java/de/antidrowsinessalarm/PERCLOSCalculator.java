package de.antidrowsinessalarm;

import android.support.annotation.NonNull;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Interval;

import java.util.List;

import de.antidrowsinessalarm.event.SlowEyelidClosureEvent;

public class PERCLOSCalculator {

    private final Duration timeWindow;

    public PERCLOSCalculator(final Duration timeWindow) {
        this.timeWindow = timeWindow;
    }

    public double calculatePERCLOS(final List<SlowEyelidClosureEvent> events, final Instant timewindowEnd) {
        Duration sumDurations = this.getSumDurations(events, this.getTimeWindowInterval(timewindowEnd));
        return (double) sumDurations.getMillis() / (double) this.timeWindow.getMillis();
    }

    @NonNull
    private Interval getTimeWindowInterval(final Instant timewindowEnd) {
        return new Interval(this.timeWindow, timewindowEnd);
    }

    private Duration getSumDurations(final List<SlowEyelidClosureEvent> events, final Interval timeWindowInterval) {
        Duration sumDurations = new Duration(0);
        for(SlowEyelidClosureEvent event : events) {
            sumDurations = sumDurations.plus(this.getIntersectionWithTimewindow(event, timeWindowInterval));
        }
        return sumDurations;
    }

    private Duration getIntersectionWithTimewindow(final SlowEyelidClosureEvent event, final Interval timeWindowInterval) {
        Interval overlap = timeWindowInterval.overlap(event.getInterval());
        return overlap != null ? overlap.toDuration() : new Duration(0);
    }
}
