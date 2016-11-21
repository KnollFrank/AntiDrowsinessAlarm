package de.antidrowsinessalarm.eventproducer;

import com.google.common.eventbus.EventBus;

import de.antidrowsinessalarm.GraphicFaceTracker;
import de.antidrowsinessalarm.listener.EventLogger;

public class DrowsyEventDetector {

    private final EventBus eventBus;
    private final DrowsyEventProducer drowsyEventProducer;
    private final GraphicFaceTracker graphicFaceTracker;

    public DrowsyEventDetector() {
        this.eventBus = new EventBus();
        this.eventBus.register(new ConsecutiveUpdateEventsProducer(this.eventBus));
        this.eventBus.register(new EyesOpenedEventProducer(this.eventBus));
        this.eventBus.register(new EyesClosedEventProducer(this.eventBus));
        this.eventBus.register(new NormalEyeBlinkEventProducer(this.eventBus));
        this.eventBus.register(new SlowEyelidClosureEventProducer(this.eventBus));
        this.eventBus.register(new EventLogger());
        SlowEyelidClosureEventsProvider slowEyelidClosureEventsProvider = new SlowEyelidClosureEventsProvider();
        this.eventBus.register(slowEyelidClosureEventsProvider);

        this.drowsyEventProducer = new DrowsyEventProducer(this.eventBus, 15000, slowEyelidClosureEventsProvider);
        this.graphicFaceTracker = new GraphicFaceTracker(this.eventBus, this.drowsyEventProducer);
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
