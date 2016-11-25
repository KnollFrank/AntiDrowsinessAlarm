package de.antidrowsinessalarm;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.eventbus.Subscribe;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import de.antidrowsinessalarm.event.Event;
import de.antidrowsinessalarm.event.EyesClosedEvent;
import de.antidrowsinessalarm.event.EyesOpenedEvent;
import de.antidrowsinessalarm.event.NormalEyeBlinkEvent;
import de.antidrowsinessalarm.event.SlowEyelidClosureEvent;
import de.antidrowsinessalarm.eventproducer.DrowsyEventDetector;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

@RunWith(AndroidJUnit4.class)
public class EventTest {

    private Context appContext;
    private EventListener listener;
    private DrowsyEventDetector drowsyEventDetector;
    private FaceDetector detector;

    @Before
    public void setup() {
        this.appContext = InstrumentationRegistry.getTargetContext();
        this.drowsyEventDetector = new DrowsyEventDetector();
        this.listener = new EventListener();
        this.drowsyEventDetector.getEventBus().register(this.listener);
        this.detector =
                FaceTrackerActivity.createFaceDetector(
                        this.appContext,
                        new MultiProcessor.Builder<>(this.createFactory()).build());
    }

    @Test
    public void testOpen() {
        // When
        this.detectorConsumesImage(R.drawable.eyes_opened, 0);

        // Then
        assertThat(this.listener.getEvents(), contains(instanceOf(EyesOpenedEvent.class)));
    }

    @Test
    public void testClose() {
        // When
        this.detectorConsumesImage(R.drawable.eyes_closed, 0);

        // Then
        assertThat(this.listener.getEvents(), contains(instanceOf(EyesClosedEvent.class)));
    }

    @Test
    public void testOpenClose() {
        // When
        this.detectorConsumesImage(R.drawable.eyes_opened, 0);
        this.detectorConsumesImage(R.drawable.eyes_closed, 1000);

        // Then
        assertThat(
                this.listener.getEvents(),
                contains(
                        instanceOf(EyesOpenedEvent.class),
                        instanceOf(EyesClosedEvent.class)));
    }

    @Test
    public void testOpenCloseOpen() {
        // When
        this.detectorConsumesImage(R.drawable.eyes_opened, 0);
        this.detectorConsumesImage(R.drawable.eyes_closed, 1000);
        this.detectorConsumesImage(R.drawable.eyes_opened, 2000);

        // Then
        assertThat(
                this.filterListenerEventsBy(EyesOpenedEvent.class, EyesClosedEvent.class),
                contains(
                        instanceOf(EyesOpenedEvent.class),
                        instanceOf(EyesClosedEvent.class),
                        instanceOf(EyesOpenedEvent.class)));
    }

    @Test
    public void shouldCreateSlowEyelidClosureEvent() {
        // When
        this.detectorConsumesImage(R.drawable.eyes_closed, 0);
        this.detectorConsumesImage(R.drawable.eyes_opened, 501);

        // Then
        assertThat(this.filterListenerEventsBy(SlowEyelidClosureEvent.class), contains(instanceOf(SlowEyelidClosureEvent.class)));
    }

    @Test
    public void shouldCreateNormalEyeBlinkEvent() {
        // When
        this.detectorConsumesImage(R.drawable.eyes_closed, 0);
        this.detectorConsumesImage(R.drawable.eyes_opened, 499);

        // Then
        assertThat(this.filterListenerEventsBy(NormalEyeBlinkEvent.class), contains(instanceOf(NormalEyeBlinkEvent.class)));
    }

    @Test
    public void shouldCreateSlowEyelidClosureEventAndNormalEyeBlinkEvent() {
        // When
        this.detectorConsumesImage(R.drawable.eyes_closed, 0);
        this.detectorConsumesImage(R.drawable.eyes_opened, 501);
        this.detectorConsumesImage(R.drawable.eyes_closed, 600);
        this.detectorConsumesImage(R.drawable.eyes_opened, 600 + 499);

        // Then
        assertThat(
                this.filterListenerEventsBy(SlowEyelidClosureEvent.class, NormalEyeBlinkEvent.class),
                contains(instanceOf(SlowEyelidClosureEvent.class), instanceOf(NormalEyeBlinkEvent.class)));
    }

    private List<Event> filterListenerEventsBy(final Class... eventClasses) {
        return this
                .getListenerEvents()
                .filter(this.isInstanceOfAny(eventClasses))
                .toList();
    }

    @NonNull
    private FluentIterable<Event> getListenerEvents() {
        return FluentIterable.from(this.listener.getEvents());
    }

    @NonNull
    private Predicate<Event> isInstanceOfAny(final Class[] eventClasses) {
        return new Predicate<Event>() {

            @Override
            public boolean apply(@Nullable final Event listenerEvent) {
                return this.isInstanceOfAny(listenerEvent, eventClasses);
            }

            private boolean isInstanceOfAny(final @Nullable Event listenerEvent, final Class[] eventClasses) {
                return FluentIterable
                        .from(eventClasses)
                        .anyMatch(this.isInstanceOf(listenerEvent));
            }

            @NonNull
            private Predicate<Class> isInstanceOf(final @Nullable Event listenerEvent) {
                return new Predicate<Class>() {

                    @Override
                    public boolean apply(@Nullable final Class eventClass) {
                        return eventClass.isInstance(listenerEvent);
                    }
                };
            }
        };
    }

    private void detectorConsumesImage(final int imageResource, final int millis) {
        this.detector.receiveFrame(this.createFrame(imageResource, millis));
    }

    private Frame createFrame(final int imageResource, final int millis) {
        Bitmap bitmap = this.getBitmap(imageResource);
        return new Frame
                .Builder()
                .setBitmap(bitmap)
                .setTimestampMillis(millis)
                .build();
    }

    private Bitmap getBitmap(final int imageResource) {
        return BitmapFactory.decodeResource(this.appContext.getResources(), imageResource);
    }

    @NonNull
    private MultiProcessor.Factory<Face> createFactory() {
        return new MultiProcessor.Factory<Face>() {

            @Override
            public Tracker<Face> create(final Face face) {
                return EventTest.this.drowsyEventDetector.getGraphicFaceTracker();
            }
        };
    }

    static class EventListener {

        private final List<Event> events = new ArrayList<Event>();

        @Subscribe
        public void recordEvent(final Event event) {
            if(event instanceof EyesOpenedEvent || event instanceof EyesClosedEvent || event instanceof SlowEyelidClosureEvent || event instanceof NormalEyeBlinkEvent) {
                this.events.add(event);
            }
        }

        Event getEvent() {
            return !this.events.isEmpty() ? this.events.get(this.events.size() - 1) : null;
        }

        List<Event> getEvents() {
            return this.events;
        }
    }
}
