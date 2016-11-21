package com.google.android.gms.samples.vision.face.facetracker;

import com.google.android.gms.samples.vision.face.facetracker.event.SlowEyelidClosureEvent;

import java.util.List;

public class PERCLOSCalculator {

    public double calculatePERCLOS(final List<SlowEyelidClosureEvent> events, final long timeWindowMillis) {
        return (double) this.getSumDurationsMillis(events) / (double) timeWindowMillis;
    }

    private long getSumDurationsMillis(final List<SlowEyelidClosureEvent> events) {
        long sum = 0;
        for(SlowEyelidClosureEvent event : events) {
            sum += event.getDurationMillis();
        }
        return sum;
    }
}
