package de.antidrowsinessalarm.eventproducer;

import com.google.common.base.MoreObjects;
import com.google.common.eventbus.EventBus;

import org.joda.time.Duration;

import de.antidrowsinessalarm.Clock;
import de.antidrowsinessalarm.GraphicFaceTracker;
import de.antidrowsinessalarm.listener.EventLogger;

public class DrowsyEventDetector {

    private final EventBus eventBus;
    private final DrowsyEventProducer drowsyEventProducer;
    private final GraphicFaceTracker graphicFaceTracker;

    public DrowsyEventDetector(final Config config, final boolean registerEventLogger, final Clock clock) {
        this.eventBus = new EventBus();
        if (registerEventLogger) {
            this.eventBus.register(new EventLogger());
        }
        this.eventBus.register(new EyesOpenedEventProducer(config.getEyeOpenProbabilityThreshold(), this.eventBus));
        this.eventBus.register(new EyesClosedEventProducer(config.getEyeOpenProbabilityThreshold(), this.eventBus));
        this.eventBus.register(new NormalEyeBlinkEventProducer(config.getSlowEyelidClosureMinDuration(), this.eventBus));
        this.eventBus.register(new SlowEyelidClosureEventProducer(config.getSlowEyelidClosureMinDuration(), this.eventBus));
        this.eventBus.register(new PendingSlowEyelidClosureEventProducer(config.getEyeOpenProbabilityThreshold(), config.getSlowEyelidClosureMinDuration(), this.eventBus));
        SlowEyelidClosureEventsProvider slowEyelidClosureEventsProvider = new SlowEyelidClosureEventsProvider(config.getTimeWindow());
        this.eventBus.register(slowEyelidClosureEventsProvider);

        this.drowsyEventProducer = new DrowsyEventProducer(config.getConfig(), this.eventBus, slowEyelidClosureEventsProvider);
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

    public static class Config {

        private final float eyeOpenProbabilityThreshold;
        private final DrowsyEventProducer.Config config;
        private final Duration slowEyelidClosureMinDuration;
        private final Duration timeWindow;

        private Config(ConfigBuilder builder) {
            this.eyeOpenProbabilityThreshold = builder.eyeOpenProbabilityThreshold;
            this.config = builder.config;
            this.slowEyelidClosureMinDuration = builder.slowEyelidClosureMinDuration;
            this.timeWindow = builder.timeWindow;
        }

        public static ConfigBuilder builder() {
            return new ConfigBuilder();
        }

        public float getEyeOpenProbabilityThreshold() {
            return this.eyeOpenProbabilityThreshold;
        }

        public DrowsyEventProducer.Config getConfig() {
            return this.config;
        }

        public Duration getSlowEyelidClosureMinDuration() {
            return this.slowEyelidClosureMinDuration;
        }

        public Duration getTimeWindow() {
            return this.timeWindow;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("eyeOpenProbabilityThreshold", eyeOpenProbabilityThreshold)
                    .add("config", config)
                    .add("slowEyelidClosureMinDuration", slowEyelidClosureMinDuration)
                    .add("timeWindow", timeWindow)
                    .toString();
        }

        public static class ConfigBuilder {

            private float eyeOpenProbabilityThreshold;
            private DrowsyEventProducer.Config config;
            private Duration slowEyelidClosureMinDuration;
            private Duration timeWindow;

            private ConfigBuilder() {
            }

            public ConfigBuilder withEyeOpenProbabilityThreshold(final float eyeOpenProbabilityThreshold) {
                this.eyeOpenProbabilityThreshold = eyeOpenProbabilityThreshold;
                return this;
            }

            public ConfigBuilder withConfig(final DrowsyEventProducer.Config config) {
                this.config = config;
                return this;
            }

            public ConfigBuilder withSlowEyelidClosureMinDuration(final Duration slowEyelidClosureMinDuration) {
                this.slowEyelidClosureMinDuration = slowEyelidClosureMinDuration;
                return this;
            }

            public ConfigBuilder withTimeWindow(final Duration timeWindow) {
                this.timeWindow = timeWindow;
                return this;
            }

            public DrowsyEventDetector.Config build() {
                return new DrowsyEventDetector.Config(this);
            }
        }
    }
}
