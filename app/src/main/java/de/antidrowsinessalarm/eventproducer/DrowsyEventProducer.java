package de.antidrowsinessalarm.eventproducer;

import com.google.common.eventbus.EventBus;

import de.antidrowsinessalarm.PERCLOSCalculator;
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

    public void maybeProduceDrowsyEvent(final long nowMillis) {
        double perclos = this.getPerclos(nowMillis);
        if(perclos >= DROWSY_THRESHOLD) {
            this.postEvent(new DrowsyEvent(nowMillis, perclos));
        } else if(perclos >= LIKELY_DROWSY_THRESHOLD) {
            this.postEvent(new LikelyDrowsyEvent(nowMillis, perclos));
        }
    }

    private double getPerclos(long nowMillis) {
        return new PERCLOSCalculator()
                .calculatePERCLOS(
                        this.slowEyelidClosureEventsProvider.getRecordedEventsPartlyWithinTimeWindow(nowMillis),
                        this.slowEyelidClosureEventsProvider.getTimeWindowMillis());
    }
}
