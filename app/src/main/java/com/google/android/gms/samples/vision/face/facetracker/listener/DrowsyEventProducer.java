package com.google.android.gms.samples.vision.face.facetracker.listener;

import com.google.android.gms.samples.vision.face.facetracker.PERCLOSCalculator;
import com.google.android.gms.samples.vision.face.facetracker.event.DrowsyEvent;
import com.google.android.gms.samples.vision.face.facetracker.event.LikelyDrowsyEvent;
import com.google.android.gms.samples.vision.face.facetracker.event.SlowEyelidClosureEvent;
import com.google.common.eventbus.EventBus;

import java.util.List;

public class DrowsyEventProducer extends EventProducer {

    private static final double DROWSY_THRESHOLD = 0.15;
    private static final double LIKELY_DROWSY_THRESHOLD = 0.08;

    private final long timeWindowMillis;
    private final SlowEyelidClosureEventsProvider slowEyelidClosureEventsProvider;

    public DrowsyEventProducer(final EventBus eventBus, final long timeWindowMillis, final SlowEyelidClosureEventsProvider slowEyelidClosureEventsProvider) {
        super(eventBus);
        this.timeWindowMillis = timeWindowMillis;
        this.slowEyelidClosureEventsProvider = slowEyelidClosureEventsProvider;
    }

    public void maybeProduceDrowsyEvent(final long nowMillis) {
        double perclos = this.getPerclos(nowMillis);
        if(perclos >= DROWSY_THRESHOLD) {
            this.postEvent(new DrowsyEvent(nowMillis, perclos));
        } else if(perclos >= LIKELY_DROWSY_THRESHOLD) {
            this.postEvent(new LikelyDrowsyEvent(nowMillis, perclos));
        }
    }

    private double getPerclos(long nowMillis) {
        List<SlowEyelidClosureEvent> recordedEventsWithinTimeWindow = this.slowEyelidClosureEventsProvider.getRecordedEventsWithinTimeWindow(nowMillis, this.timeWindowMillis);
        return new PERCLOSCalculator().calculatePERCLOS(recordedEventsWithinTimeWindow, this.timeWindowMillis);
    }
}
