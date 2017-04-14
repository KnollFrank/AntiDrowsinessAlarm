package de.drowsydriveralarm.eventproducer;

import com.google.common.eventbus.EventBus;

import de.drowsydriveralarm.Clock;
import de.drowsydriveralarm.listener.EventLogger;

public class DrowsyEventDetector {

    private final EventBus eventBus;
    private final DrowsyEventProducer drowsyEventProducer;
    private final EventProducingGraphicFaceTracker eventProducingGraphicFaceTracker;

    public DrowsyEventDetector(final IDrowsyEventDetectorConfig drowsyEventDetectorConfig, final boolean registerEventLogger, final Clock clock) {
        this.eventBus = new EventBus();
        if (registerEventLogger) {
            this.eventBus.register(new EventLogger());
        }
        final SlowEyelidClosureEventsProvider slowEyelidClosureEventsProvider = new SlowEyelidClosureEventsProvider(drowsyEventDetectorConfig.getTimeWindow());
        registerEventProducersOnEventBus(this.eventBus, drowsyEventDetectorConfig, slowEyelidClosureEventsProvider);

        this.drowsyEventProducer = new DrowsyEventProducer(drowsyEventDetectorConfig.getConfig(), this.eventBus, slowEyelidClosureEventsProvider);
        this.eventProducingGraphicFaceTracker = new EventProducingGraphicFaceTracker(this.eventBus, this.drowsyEventProducer, clock);
    }

    static void registerEventProducersOnEventBus(final EventBus eventBus, final IDrowsyEventDetectorConfig drowsyEventDetectorConfig, final SlowEyelidClosureEventsProvider slowEyelidClosureEventsProvider) {
        eventBus.register(new EyesOpenedEventProducer(drowsyEventDetectorConfig.getEyeOpenProbabilityThreshold(), eventBus));
        eventBus.register(new EyesClosedEventProducer(drowsyEventDetectorConfig.getEyeOpenProbabilityThreshold(), eventBus));
        eventBus.register(new NormalEyeBlinkEventProducer(drowsyEventDetectorConfig.getSlowEyelidClosureMinDuration(), eventBus));
        eventBus.register(new SlowEyelidClosureEventProducer(drowsyEventDetectorConfig.getSlowEyelidClosureMinDuration(), eventBus));
        eventBus.register(new PendingSlowEyelidClosureEventProducer(drowsyEventDetectorConfig.getEyeOpenProbabilityThreshold(), drowsyEventDetectorConfig.getSlowEyelidClosureMinDuration(), eventBus));
        eventBus.register(slowEyelidClosureEventsProvider);
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
