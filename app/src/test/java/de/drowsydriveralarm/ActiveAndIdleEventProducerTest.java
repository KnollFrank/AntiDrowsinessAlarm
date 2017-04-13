package de.drowsydriveralarm;

import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.common.eventbus.EventBus;

import org.joda.time.Instant;
import org.junit.Before;
import org.junit.Test;

import de.drowsydriveralarm.event.AppActiveEvent;
import de.drowsydriveralarm.event.AppIdleEvent;
import de.drowsydriveralarm.eventproducer.ActiveAndIdleEventProducer;
import de.drowsydriveralarm.eventproducer.DefaultConfigFactory;
import de.drowsydriveralarm.eventproducer.DrowsyEventProducer;
import de.drowsydriveralarm.eventproducer.EyesClosedEventProducer;
import de.drowsydriveralarm.eventproducer.EyesOpenedEventProducer;
import de.drowsydriveralarm.eventproducer.NormalEyeBlinkEventProducer;
import de.drowsydriveralarm.eventproducer.SlowEyelidClosureEventProducer;
import de.drowsydriveralarm.eventproducer.SlowEyelidClosureEventsProvider;

import static de.drowsydriveralarm.EventProducingGraphicFaceTrackerTest.createFaceWithEyesClosed;
import static de.drowsydriveralarm.EventProducingGraphicFaceTrackerTest.getFaceDetections;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.core.Is.isA;

public class ActiveAndIdleEventProducerTest {

    private EventListener eventListener;
    private Tracker<Face> tracker;

    @Before
    public void setup() {
        // Given
        this.eventListener = new EventListener();
        final EventBus eventBus = new EventBus();
        eventBus.register(this.eventListener);
        final DefaultConfigFactory configFactory = new DefaultConfigFactory(SharedPreferencesTestFactory.createSharedPreferences());
        // TODO: DRY with DrowsyEventDetector
        eventBus.register(new NormalEyeBlinkEventProducer(configFactory.getSlowEyelidClosureMinDuration(), eventBus));
        eventBus.register(new SlowEyelidClosureEventProducer(configFactory.getSlowEyelidClosureMinDuration(), eventBus));
        eventBus.register(new EyesOpenedEventProducer(configFactory.getEyeOpenProbabilityThreshold(), eventBus));
        eventBus.register(new EyesClosedEventProducer(configFactory.getEyeOpenProbabilityThreshold(), eventBus));

        this.tracker =
                new CompositeFaceTracker(
                        new ActiveAndIdleEventProducer(eventBus),
                        new EventProducingGraphicFaceTracker(
                                eventBus,
                                new DrowsyEventProducer(
                                        configFactory.getConfig(),
                                        eventBus,
                                        new SlowEyelidClosureEventsProvider(configFactory.getTimeWindow())),
                                new SystemClock()));
    }

    @Test
    public void shouldCreateAppActiveEvent_onNewItem() {
        // When
        this.tracker.onNewItem(1, createFaceWithEyesClosed());

        // Then
        assertThat(this.eventListener.getEvents(), hasItem(isA(AppActiveEvent.class)));
    }

    @Test
    public void shouldCreateAppIdleEvent_onMissing() {
        // When
        this.tracker.onMissing(getFaceDetections(new Instant(501)));

        // Then
        assertThat(this.eventListener.getEvents(), hasItem(isA(AppIdleEvent.class)));
    }

    @Test
    public void shouldCreateAppIdleEvent_onDone() {
        // When
        this.tracker.onDone();

        // Then
        assertThat(this.eventListener.getEvents(), hasItem(isA(AppIdleEvent.class)));
    }
}
