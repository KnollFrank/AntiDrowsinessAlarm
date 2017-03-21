package de.antidrowsinessalarm;

import android.content.SharedPreferences;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame.Metadata;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.common.eventbus.EventBus;

import org.hamcrest.Matchers;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import de.antidrowsinessalarm.event.Event;
import de.antidrowsinessalarm.event.EyesClosedEvent;
import de.antidrowsinessalarm.event.EyesOpenedEvent;
import de.antidrowsinessalarm.event.NormalEyeBlinkEvent;
import de.antidrowsinessalarm.event.SlowEyelidClosureEvent;
import de.antidrowsinessalarm.eventproducer.DefaultConfigFactory;
import de.antidrowsinessalarm.eventproducer.DrowsyEventProducer;
import de.antidrowsinessalarm.eventproducer.EyesClosedEventProducer;
import de.antidrowsinessalarm.eventproducer.EyesOpenedEventProducer;
import de.antidrowsinessalarm.eventproducer.NormalEyeBlinkEventProducer;
import de.antidrowsinessalarm.eventproducer.SlowEyelidClosureEventProducer;
import de.antidrowsinessalarm.eventproducer.SlowEyelidClosureEventsProvider;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

public class GraphicFaceTrackerTest {

    private EventListener eventListener;
    private Tracker<Face> tracker;

    static Detector.Detections<Face> getFaceDetections(final Instant instant) {
        final Metadata metaData = Mockito.mock(Metadata.class);
        doReturn(instant.getMillis()).when(metaData).getTimestampMillis();

        final Detector.Detections<Face> detections = Mockito.mock(Detector.Detections.class);
        doReturn(metaData).when(detections).getFrameMetadata();

        return detections;
    }

    static Face createFaceWithEyesClosed() {
        return createFace(0.4f, 0.4f);
    }

    static Face createFace(final float isLeftEyeOpenProbability, final float isRightEyeOpenProbability) {
        final Face face = Mockito.mock(Face.class);
        doReturn(isLeftEyeOpenProbability).when(face).getIsLeftEyeOpenProbability();
        doReturn(isRightEyeOpenProbability).when(face).getIsRightEyeOpenProbability();
        return face;
    }

    static Face createFaceWithEyesOpened() {
        return createFace(0.8f, 0.8f);
    }

    @Before
    public void setup() {
        // Given
        this.eventListener = new EventListener();
        final EventBus eventBus = new EventBus();
        eventBus.register(this.eventListener);
        eventBus.register(new NormalEyeBlinkEventProducer(DefaultConfigFactory.getSlowEyelidClosureMinDuration(), eventBus));
        eventBus.register(new SlowEyelidClosureEventProducer(DefaultConfigFactory.getSlowEyelidClosureMinDuration(), eventBus));
        eventBus.register(new EyesOpenedEventProducer(DefaultConfigFactory.getEyeOpenProbabilityThreshold(), eventBus));
        eventBus.register(new EyesClosedEventProducer(DefaultConfigFactory.getEyeOpenProbabilityThreshold(), eventBus));

        // TODO: DRY
        final SharedPreferences sharedPreferences = Mockito.mock(SharedPreferences.class);
        when(sharedPreferences.getString(eq("drowsyThreshold"), anyString())).thenReturn("0.15");
        this.tracker =
                new GraphicFaceTracker(
                        eventBus,
                        new DrowsyEventProducer(
                                DefaultConfigFactory.getConfig(sharedPreferences),
                                eventBus,
                                new SlowEyelidClosureEventsProvider(DefaultConfigFactory.getTimeWindow())),
                        new SystemClock());
    }

    @Test
    public void shouldCreateEyesClosedEvent() {
        this.shouldCreateEvent(0.4f, 0.4f, new EyesClosedEvent(new Instant(100)));
    }

    @Test
    public void shouldCreateEyesClosedEvent2() {
        this.shouldCreateEvent(0.3f, 0.4f, new EyesClosedEvent(new Instant(101)));
    }

    private void shouldCreateEvent(final float isLeftEyeOpenProbability, final float isRightEyeOpenProbability, final Event event) {
        // When
        this.tracker.onUpdate(getFaceDetections(event.getInstant()), createFace(isLeftEyeOpenProbability, isRightEyeOpenProbability));

        // Then
        assertThat(this.eventListener.getEvents(), hasItem(event));
    }

    @Test
    public void shouldNotCreateEyesClosedEventOnUNCOMPUTED_PROBABILITIES() {
        // When
        this.tracker.onUpdate(getFaceDetections(new Instant(100)), createFace(Face.UNCOMPUTED_PROBABILITY, Face.UNCOMPUTED_PROBABILITY));

        // Then
        assertThat(this.eventListener.getEvents(), not(hasItem(Matchers.<Event>instanceOf(EyesClosedEvent.class))));
    }

    @Test
    public void shouldNotCreateEyesOpenedEventOnUNCOMPUTED_PROBABILITIES() {
        // When
        this.tracker.onUpdate(getFaceDetections(new Instant(100)), createFace(Face.UNCOMPUTED_PROBABILITY, Face.UNCOMPUTED_PROBABILITY));

        // Then
        assertThat(this.eventListener.getEvents(), not(hasItem(Matchers.<Event>instanceOf(EyesOpenedEvent.class))));
    }

    @Test
    public void shouldCreateEyesOpenedEvent() {
        this.shouldCreateEvent(0.8f, 0.8f, new EyesOpenedEvent(new Instant(123)));
    }

    @Test
    public void shouldCreateEyesOpenedEvent2() {
        this.shouldCreateEvent(0.8f, 0.9f, new EyesOpenedEvent(new Instant(1234)));
    }

    @Test
    public void shouldCreateNormalEyeBlink() {
        // When
        this.tracker.onUpdate(getFaceDetections(new Instant(0)), createFaceWithEyesClosed());
        this.tracker.onUpdate(getFaceDetections(new Instant(499)), createFaceWithEyesOpened());

        // Then
        assertThat(this.eventListener.getEvents(), hasItem(new NormalEyeBlinkEvent(new Instant(0), new Duration(499))));
    }

    @Test
    public void shouldCreateSlowEyelidClosureEvent() {
        // When
        this.tracker.onUpdate(getFaceDetections(new Instant(0)), createFaceWithEyesClosed());
        this.tracker.onUpdate(getFaceDetections(new Instant(501)), createFaceWithEyesOpened());

        // Then
        assertThat(this.eventListener.getEvents(), hasItem(new SlowEyelidClosureEvent(new Instant(0), new Duration(501))));
    }

    @Test
    public void shouldCreateASingleEyesOpenedEventForIntermediateIndefiniteEyesState() {
        // When
        this.tracker.onUpdate(getFaceDetections(new Instant(0)), createFaceWithEyesClosed());
        this.tracker.onUpdate(getFaceDetections(new Instant(1)), createFaceWithEyesOpened());
        this.tracker.onUpdate(getFaceDetections(new Instant(2)), this.createFaceWithLeftEyeOpenRightEyeClosed());
        this.tracker.onUpdate(getFaceDetections(new Instant(3)), createFaceWithEyesOpened());

        // Then
        assertThat(this.eventListener.filterEventsBy(EyesOpenedEvent.class), contains(new EyesOpenedEvent(new Instant(1))));
    }

    @Test
    public void shouldCreateASingleEyesClosedEventForIntermediateIndefiniteEyesState() {
        // When
        this.tracker.onUpdate(getFaceDetections(new Instant(0)), createFaceWithEyesOpened());
        this.tracker.onUpdate(getFaceDetections(new Instant(1)), createFaceWithEyesClosed());
        this.tracker.onUpdate(getFaceDetections(new Instant(2)), this.createFaceWithLeftEyeOpenRightEyeClosed());
        this.tracker.onUpdate(getFaceDetections(new Instant(3)), createFaceWithEyesClosed());

        // Then
        assertThat(this.eventListener.filterEventsBy(EyesClosedEvent.class), contains(new EyesClosedEvent(new Instant(1))));
    }

    @Test
    public void shouldCreateASingleEyesClosedEvent() {
        // When
        this.tracker.onUpdate(getFaceDetections(new Instant(100)), createFaceWithEyesClosed());
        this.tracker.onUpdate(getFaceDetections(new Instant(101)), createFaceWithEyesClosed());

        // Then
        assertThat(this.eventListener.filterEventsBy(EyesClosedEvent.class), contains(new EyesClosedEvent(new Instant(100))));
    }

    @Test
    public void shouldCreateASingleEyesOpenedEvent() {
        // When
        this.tracker.onUpdate(getFaceDetections(new Instant(100)), createFaceWithEyesOpened());
        this.tracker.onUpdate(getFaceDetections(new Instant(101)), createFaceWithEyesOpened());

        // Then
        assertThat(this.eventListener.filterEventsBy(EyesOpenedEvent.class), contains(new EyesOpenedEvent(new Instant(100))));
    }

    @Test
    public void shouldCreateEvents() {
        // When
        this.tracker.onNewItem(1, createFaceWithEyesOpened());
        this.tracker.onUpdate(getFaceDetections(new Instant(100)), createFaceWithEyesOpened());
        this.tracker.onUpdate(getFaceDetections(new Instant(101)), createFaceWithEyesClosed());
        this.tracker.onUpdate(getFaceDetections(new Instant(102)), createFaceWithEyesOpened());
        this.tracker.onUpdate(getFaceDetections(new Instant(103)), createFaceWithEyesClosed());

        // Then
        assertThat(this.eventListener.getEvents(), hasItems(
                new EyesOpenedEvent(new Instant(100)),
                new EyesClosedEvent(new Instant(101)),
                new EyesOpenedEvent(new Instant(102)),
                new EyesClosedEvent(new Instant(103))));
    }

    @Test
    public void shouldCreateEvents2() {
        // When
        this.tracker.onNewItem(1, createFaceWithEyesClosed());
        this.tracker.onUpdate(getFaceDetections(new Instant(100)), createFaceWithEyesClosed());
        this.tracker.onUpdate(getFaceDetections(new Instant(101)), createFaceWithEyesOpened());
        this.tracker.onUpdate(getFaceDetections(new Instant(102)), createFaceWithEyesClosed());
        this.tracker.onUpdate(getFaceDetections(new Instant(103)), createFaceWithEyesOpened());

        // Then
        assertThat(this.eventListener.getEvents(), hasItems(
                new EyesClosedEvent(new Instant(100)),
                new EyesOpenedEvent(new Instant(101)),
                new EyesClosedEvent(new Instant(102)),
                new EyesOpenedEvent(new Instant(103))));
    }

    @Test
    public void shouldCreateEvents3() {
        // When
        this.tracker.onNewItem(1, createFaceWithEyesClosed());
        this.tracker.onUpdate(getFaceDetections(new Instant(100)), createFaceWithEyesClosed());
        this.tracker.onUpdate(getFaceDetections(new Instant(101)), createFaceWithEyesClosed());
        this.tracker.onUpdate(getFaceDetections(new Instant(102)), createFaceWithEyesOpened());
        this.tracker.onUpdate(getFaceDetections(new Instant(103)), createFaceWithEyesOpened());
        this.tracker.onUpdate(getFaceDetections(new Instant(104)), createFaceWithEyesClosed());
        this.tracker.onUpdate(getFaceDetections(new Instant(105)), createFaceWithEyesClosed());

        // Then
        assertThat(this.eventListener.getEvents(), hasItems(
                new EyesClosedEvent(new Instant(100)),
                new EyesOpenedEvent(new Instant(102)),
                new EyesClosedEvent(new Instant(104))));
    }

    private Face createFaceWithLeftEyeOpenRightEyeClosed() {
        return createFace(0.8f, 0.4f);
    }

}