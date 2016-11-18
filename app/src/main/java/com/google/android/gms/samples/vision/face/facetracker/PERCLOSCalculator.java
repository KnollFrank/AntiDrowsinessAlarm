package com.google.android.gms.samples.vision.face.facetracker;

import com.google.android.gms.samples.vision.face.facetracker.event.SlowEyelidClosureEvent;

import java.util.List;

class PERCLOSCalculator {

    public double calculatePERCLOS(List<SlowEyelidClosureEvent> slowEyelidClosureEvents, long timeWindowMillis) {
        return (double) this.getSumDurationsMillis(slowEyelidClosureEvents) / (double) timeWindowMillis;
    }

    private long getSumDurationsMillis(List<SlowEyelidClosureEvent> slowEyelidClosureEvents) {
        long sumDurationsMillis = 0;
        for(SlowEyelidClosureEvent slowEyelidClosureEvent : slowEyelidClosureEvents) {
            sumDurationsMillis += slowEyelidClosureEvent.getDurationMillis();
        }
        return sumDurationsMillis;
    }
}
