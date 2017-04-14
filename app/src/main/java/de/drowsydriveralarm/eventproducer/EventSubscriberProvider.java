package de.drowsydriveralarm.eventproducer;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;

import java.util.Collection;

class EventSubscriberProvider {

    private final Collection<Object> eventSubscribers;
    private final SlowEyelidClosureEventsProvider slowEyelidClosureEventsProvider;

    public EventSubscriberProvider(final EventBus eventBus, final IDrowsyEventDetectorConfig config) {
        this.slowEyelidClosureEventsProvider = new SlowEyelidClosureEventsProvider(config.getTimeWindow());
        this.eventSubscribers =
                ImmutableList.
                        builder()
                        .add(new EyesOpenedEventProducer(config.getEyeOpenProbabilityThreshold(), eventBus))
                        .add(new EyesClosedEventProducer(config.getEyeOpenProbabilityThreshold(), eventBus))
                        .add(new NormalEyeBlinkEventProducer(config.getSlowEyelidClosureMinDuration(), eventBus))
                        .add(new SlowEyelidClosureEventProducer(config.getSlowEyelidClosureMinDuration(), eventBus))
                        .add(new PendingSlowEyelidClosureEventProducer(config.getEyeOpenProbabilityThreshold(), config.getSlowEyelidClosureMinDuration(), eventBus))
                        .add(this.slowEyelidClosureEventsProvider)
                        .build();
    }

    public Collection<Object> getEventSubscribers() {
        return this.eventSubscribers;
    }

    public SlowEyelidClosureEventsProvider getSlowEyelidClosureEventsProvider() {
        return this.slowEyelidClosureEventsProvider;
    }
}
