package com.google.android.gms.samples.vision.face.facetracker.listener;

import com.google.android.gms.samples.vision.face.facetracker.GraphicFaceTracker;
import com.google.common.eventbus.EventBus;

public class DrowsyEventDetector {

    private final EventBus eventBus;
    private final SlowEyelidClosureEventsProvider slowEyelidClosureEventsProvider;
    private final GraphicFaceTracker graphicFaceTracker;

    public DrowsyEventDetector() {
        this.slowEyelidClosureEventsProvider = new SlowEyelidClosureEventsProvider();
        this.eventBus = new EventBus();
        this.initializeEventBus();
        this.graphicFaceTracker = new GraphicFaceTracker(this.eventBus);
    }

    private void initializeEventBus() {
        this.eventBus.register(new EyesOpenedEventProducer(this.eventBus));
        this.eventBus.register(new EyesClosedEventProducer(this.eventBus));
        this.eventBus.register(new NormalEyeBlinkEventProducer(this.eventBus));
        this.eventBus.register(new SlowEyelidClosureEventProducer(this.eventBus));
        // DrowsyEventProducer drowsyEventProducer = new DrowsyEventProducer(this.eventBus, 2000, slowEyelidClosureEventsProvider);
        this.eventBus.register(this.slowEyelidClosureEventsProvider);
    }

    public EventBus getEventBus() {
        return this.eventBus;
    }

    public GraphicFaceTracker getGraphicFaceTracker() {
        return this.graphicFaceTracker;
    }

    public SlowEyelidClosureEventsProvider getSlowEyelidClosureEventsProvider() {
        return this.slowEyelidClosureEventsProvider;
    }
}
