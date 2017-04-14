package de.drowsydriveralarm.eventproducer;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;

import java.util.Collection;

class EventSubscriberProvider {

    private final EventBus eventBus;
    private final IDrowsyEventDetectorConfig config;
    private final Collection<Object> eventSubscribers;
    private final SlowEyelidClosureEventsProvider slowEyelidClosureEventsProvider;

    public EventSubscriberProvider(final EventBus eventBus, final IDrowsyEventDetectorConfig config) {
        this.eventBus = eventBus;
        this.config = config;
        this.slowEyelidClosureEventsProvider = new SlowEyelidClosureEventsProvider(this.config.getTimeWindow());
        this.eventSubscribers = this.createEventSubscribers(this.slowEyelidClosureEventsProvider);
    }

    private Collection<Object> createEventSubscribers(final SlowEyelidClosureEventsProvider slowEyelidClosureEventsProvider) {
        return ImmutableList.
                builder()
                .add(new EyesOpenedEventProducer(this.config.getEyeOpenProbabilityThreshold(), this.eventBus))
                .add(new EyesClosedEventProducer(this.config.getEyeOpenProbabilityThreshold(), this.eventBus))
                .add(new NormalEyeBlinkEventProducer(this.config.getSlowEyelidClosureMinDuration(), this.eventBus))
                .add(new SlowEyelidClosureEventProducer(this.config.getSlowEyelidClosureMinDuration(), this.eventBus))
                .add(new PendingSlowEyelidClosureEventProducer(this.config.getEyeOpenProbabilityThreshold(), this.config.getSlowEyelidClosureMinDuration(), this.eventBus))
                .add(slowEyelidClosureEventsProvider)
                .build();
    }

    public Collection<Object> getEventSubscribers() {
        return this.eventSubscribers;
    }

    public SlowEyelidClosureEventsProvider getSlowEyelidClosureEventsProvider() {
        return this.slowEyelidClosureEventsProvider;
    }
}
