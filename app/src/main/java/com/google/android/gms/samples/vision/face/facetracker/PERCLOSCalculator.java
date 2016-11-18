package com.google.android.gms.samples.vision.face.facetracker;

import com.google.android.gms.samples.vision.face.facetracker.event.SlowEyelidClosureEvent;

import java.util.List;

public class PERCLOSCalculator {

    public double calculatePERCLOS(final List<SlowEyelidClosureEvent> slowEyelidClosureEvents, final long timeWindowMillis) {
        return (double) this.getSumDurationsMillis(slowEyelidClosureEvents) / (double) timeWindowMillis;
    }

    private long getSumDurationsMillis(final List<SlowEyelidClosureEvent> slowEyelidClosureEvents) {
        long sumDurationsMillis = 0;
        for(SlowEyelidClosureEvent slowEyelidClosureEvent : slowEyelidClosureEvents) {
            sumDurationsMillis += slowEyelidClosureEvent.getDurationMillis();
        }
        return sumDurationsMillis;
    }
}
