package de.antidrowsinessalarm;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.common.eventbus.Subscribe;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import de.antidrowsinessalarm.event.Event;
import de.antidrowsinessalarm.event.EyesClosedEvent;
import de.antidrowsinessalarm.event.EyesOpenedEvent;
import de.antidrowsinessalarm.eventproducer.DrowsyEventDetector;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.fail;

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

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD_MR1)
    @Test
    public void shouldCreateEyesOpenedClosedEvents() {
        // When
        // this.detectorConsumesVideo(30, R.raw.slow_eyelid_closure);

        // Then
        assertThat(this.listener.getEvents(), hasSize(5));
        assertThat(this.listener.getEvents().get(0), is(instanceOf(EyesOpenedEvent.class)));
        assertThat(this.listener.getEvents().get(1), is(instanceOf(EyesClosedEvent.class)));
        assertThat(this.listener.getEvents().get(2), is(instanceOf(EyesOpenedEvent.class)));
        assertThat(this.listener.getEvents().get(3), is(instanceOf(EyesClosedEvent.class)));
        assertThat(this.listener.getEvents().get(4), is(instanceOf(EyesOpenedEvent.class)));
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
                contains(instanceOf(EyesOpenedEvent.class), instanceOf(EyesClosedEvent.class)));
    }

    @Test
    public void testOpenCloseOpen() {
        // When
        this.detectorConsumesImage(R.drawable.eyes_opened, 0);
        this.detectorConsumesImage(R.drawable.eyes_closed, 1000);
        this.detectorConsumesImage(R.drawable.eyes_opened, 2000);

        // Then
        assertThat(
                this.listener.getEvents(),
                contains(instanceOf(EyesOpenedEvent.class), instanceOf(EyesClosedEvent.class), instanceOf(EyesOpenedEvent.class)));
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

    @Test
    public void shouldCreateSlowEyelidClosureEvents() {
        fail("not yet implemented");
    }

    @Test
    public void shouldCreateNormalEyeBlinkEvents() {
        fail("not yet implemented");
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
            if(event instanceof EyesOpenedEvent || event instanceof EyesClosedEvent) {
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
