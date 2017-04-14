package de.drowsydriveralarm.eventproducer;

import com.google.common.eventbus.EventBus;

import java.util.Collection;

import de.drowsydriveralarm.Clock;
import de.drowsydriveralarm.listener.EventLogger;

public class DrowsyEventDetector {

    private final EventBus eventBus;
    private final DrowsyEventProducer drowsyEventProducer;
    private final EventProducingGraphicFaceTracker eventProducingGraphicFaceTracker;

    public DrowsyEventDetector(final IDrowsyEventDetectorConfig config, final boolean registerEventLogger, final Clock clock) {
        this.eventBus = new EventBus();
        if (registerEventLogger) {
            this.eventBus.register(new EventLogger());
        }
        final EventSubscriberProvider eventSubscriberProvider = new EventSubscriberProvider(this.eventBus, config);
        registerEventSubscribersOnEventBus(eventSubscriberProvider.getEventSubscribers(), this.eventBus);

        this.drowsyEventProducer = new DrowsyEventProducer(config.getConfig(), this.eventBus, eventSubscriberProvider.getSlowEyelidClosureEventsProvider());
        this.eventProducingGraphicFaceTracker = new EventProducingGraphicFaceTracker(this.eventBus, this.drowsyEventProducer, clock);
    }

    static void registerEventSubscribersOnEventBus(final Collection<Object> eventSubscribers, final EventBus eventBus) {
        for(final Object eventSubscriber: eventSubscribers) {
            eventBus.register(eventSubscriber);
        }
    }

    public EventBus getEventBus() {
        return this.eventBus;
    }

    public EventProducingGraphicFaceTracker getEventProducingGraphicFaceTracker() {
        return this.eventProducingGraphicFaceTracker;
    }

    public DrowsyEventProducer getDrowsyEventProducer() {
        return this.drowsyEventProducer;
    }
}
