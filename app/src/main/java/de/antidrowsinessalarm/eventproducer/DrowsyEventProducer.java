package de.antidrowsinessalarm.eventproducer;

import android.support.annotation.NonNull;

import com.google.common.eventbus.EventBus;

import org.joda.time.Instant;

import de.antidrowsinessalarm.PERCLOSCalculator;
import de.antidrowsinessalarm.event.AwakeEvent;
import de.antidrowsinessalarm.event.DrowsyEvent;
import de.antidrowsinessalarm.event.LikelyDrowsyEvent;

public class DrowsyEventProducer extends EventProducer {

    // TODO: make configurable
    private static final double DROWSY_THRESHOLD = 0.15;
    // TODO: make configurable
    private static final double LIKELY_DROWSY_THRESHOLD = 0.08;

    private final SlowEyelidClosureEventsProvider slowEyelidClosureEventsProvider;

    public DrowsyEventProducer(final EventBus eventBus, final SlowEyelidClosureEventsProvider slowEyelidClosureEventsProvider) {
        super(eventBus);
        this.slowEyelidClosureEventsProvider = slowEyelidClosureEventsProvider;
    }

    public void maybeProduceDrowsyEvent(final Instant now) {
        double perclos = this.getPerclos(now);
        if(perclos >= DROWSY_THRESHOLD) {
            this.postEvent(new DrowsyEvent(now, perclos));
        } else if(perclos >= LIKELY_DROWSY_THRESHOLD) {
            this.postEvent(new LikelyDrowsyEvent(now, perclos));
        } else {
            this.postEvent(new AwakeEvent(now, perclos));
        }
    }

    private double getPerclos(final Instant now) {
        return this.getPERCLOSCalculator().calculatePERCLOS(this.slowEyelidClosureEventsProvider.getRecordedEventsPartlyWithinTimeWindow(now), now);
    }

    @NonNull
    private PERCLOSCalculator getPERCLOSCalculator() {
        return new PERCLOSCalculator(this.slowEyelidClosureEventsProvider.getTimeWindow());
    }
}
