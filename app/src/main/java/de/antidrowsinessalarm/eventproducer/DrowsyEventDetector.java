package de.antidrowsinessalarm.eventproducer;

import com.google.common.eventbus.EventBus;

import org.joda.time.Duration;

import de.antidrowsinessalarm.Clock;
import de.antidrowsinessalarm.GraphicFaceTracker;
import de.antidrowsinessalarm.listener.EventLogger;

public class DrowsyEventDetector {

    private final EventBus eventBus;
    private final DrowsyEventProducer drowsyEventProducer;
    private final GraphicFaceTracker graphicFaceTracker;

    public DrowsyEventDetector(final Clock clock, final Duration slowEyelidClosureMinDuration, final Duration timeWindow, final boolean registerEventLogger) {
        this.eventBus = new EventBus();
        this.eventBus.register(new EyesOpenedEventProducer(this.eventBus));
        this.eventBus.register(new EyesClosedEventProducer(this.eventBus));
        this.eventBus.register(new NormalEyeBlinkEventProducer(slowEyelidClosureMinDuration, this.eventBus));
        this.eventBus.register(new SlowEyelidClosureEventProducer(ConfigFactory.getDefaultSlowEyelidClosureMinDuration(), this.eventBus));
        if(registerEventLogger) {
            this.eventBus.register(new EventLogger());
        }
        this.eventBus.register(new PendingSlowEyelidClosureEventProducer(slowEyelidClosureMinDuration, this.eventBus));
        SlowEyelidClosureEventsProvider slowEyelidClosureEventsProvider = new SlowEyelidClosureEventsProvider(timeWindow);
        this.eventBus.register(slowEyelidClosureEventsProvider);

        // TODO: ConfigFactory.createDefaultConfig() als Funktionsparameter übergeben
        this.drowsyEventProducer = new DrowsyEventProducer(ConfigFactory.createDefaultConfig(), this.eventBus, slowEyelidClosureEventsProvider);
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
