package de.antidrowsinessalarm.eventproducer;

import com.google.common.eventbus.EventBus;

import de.antidrowsinessalarm.Clock;
import de.antidrowsinessalarm.GraphicFaceTracker;
import de.antidrowsinessalarm.listener.EventLogger;

public class DrowsyEventDetector {

    private final EventBus eventBus;
    private final DrowsyEventProducer drowsyEventProducer;
    private final GraphicFaceTracker graphicFaceTracker;

    public DrowsyEventDetector(final Clock clock) {
        this.eventBus = new EventBus();
        this.eventBus.register(new ConsecutiveUpdateEventsProducer(this.eventBus));
        this.eventBus.register(new EyesOpenedEventProducer(this.eventBus));
        this.eventBus.register(new EyesClosedEventProducer(this.eventBus));
        this.eventBus.register(new NormalEyeBlinkEventProducer(this.eventBus));
        this.eventBus.register(new SlowEyelidClosureEventProducer(this.eventBus));
        this.eventBus.register(new EventLogger());
        SlowEyelidClosureEventsProvider slowEyelidClosureEventsProvider = new SlowEyelidClosureEventsProvider();
        this.eventBus.register(slowEyelidClosureEventsProvider);

        // TODO: make timeWindowMillis = 15000 configurable
        this.drowsyEventProducer = new DrowsyEventProducer(this.eventBus, 15000, slowEyelidClosureEventsProvider);
        this.graphicFaceTracker = new GraphicFaceTracker(this.eventBus, this.drowsyEventProducer, clock);
    }

    public EventBus getEventBus() {
        return this.eventBus;
    }

    public GraphicFaceTracker getGraphicFaceTracker() {
        return this.graphicFaceTracker;
    }

    public DrowsyEventProducer getDrowsyEventProducer() {
        return this.drowsyEventProducer;
    }
}
