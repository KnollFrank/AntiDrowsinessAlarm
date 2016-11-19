package com.google.android.gms.samples.vision.face.facetracker.listener;

import com.google.android.gms.samples.vision.face.facetracker.PERCLOSCalculator;
import com.google.android.gms.samples.vision.face.facetracker.event.DrowsyEvent;
import com.google.android.gms.samples.vision.face.facetracker.event.LikelyDrowsyEvent;
import com.google.android.gms.samples.vision.face.facetracker.event.SlowEyelidClosureEvent;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class DrowsyEventProducer extends EventProducer {

    private final long timeWindowMillis;
    private final List<SlowEyelidClosureEvent> slowEyelidClosureEvents = new ArrayList<SlowEyelidClosureEvent>();

    public DrowsyEventProducer(final EventBus eventBus, final long timeWindowMillis) {
        super(eventBus);
        this.timeWindowMillis = timeWindowMillis;
    }

    @Subscribe
    public void recordEyesClosedEvent(final SlowEyelidClosureEvent slowEyelidClosureEvent) {
        this.slowEyelidClosureEvents.add(slowEyelidClosureEvent);
    }

    public void maybeProduceDrowsyEvent(final long nowMillis) {
        double perclos = new PERCLOSCalculator().calculatePERCLOS(this.getSlowEyelidClosureEventsWithinTimeWindow(nowMillis), this.timeWindowMillis);
        if(perclos >= 0.15) {
            this.postEvent(new DrowsyEvent(nowMillis, perclos));
        } else if(perclos >= 0.08) {
            this.postEvent(new LikelyDrowsyEvent(nowMillis, perclos));
        }
    }

    private List<SlowEyelidClosureEvent> getSlowEyelidClosureEventsWithinTimeWindow(final long nowMillis) {
        // TODO: use Joda-Time Interval [startMillis, endMillis]
        final long startMillis = nowMillis - this.timeWindowMillis;
        final long endMillis = nowMillis;
        Predicate<SlowEyelidClosureEvent> isEventWithinTimeWindow =
                new Predicate<SlowEyelidClosureEvent>() {
                    @Override
                    public boolean apply(SlowEyelidClosureEvent event) {
                        return event.getTimestampMillis() >= startMillis && event.getTimestampMillis() + event.getDurationMillis() <= endMillis;
                    }
                };
        return FluentIterable.from(this.slowEyelidClosureEvents).filter(isEventWithinTimeWindow).toList();
    }
}
