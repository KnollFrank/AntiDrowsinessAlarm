package de.antidrowsinessalarm;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
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
import de.antidrowsinessalarm.test.R;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
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
        this.detectorConsumesVideo(30, R.raw.slow_eyelid_closure);

        // Then
        assertThat(this.listener.getEvents(), hasSize(5));
        assertThat(this.listener.getEvents().get(0), is(instanceOf(EyesOpenedEvent.class)));
        assertThat(this.listener.getEvents().get(1), is(instanceOf(EyesClosedEvent.class)));
        assertThat(this.listener.getEvents().get(2), is(instanceOf(EyesOpenedEvent.class)));
        assertThat(this.listener.getEvents().get(3), is(instanceOf(EyesClosedEvent.class)));
        assertThat(this.listener.getEvents().get(4), is(instanceOf(EyesOpenedEvent.class)));
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD_MR1)
    @Test
    public void testOpenCloseOpen() {
        // When
        this.detectorConsumesVideo(30, R.raw.open_close_open);

        // Then
        assertThat(this.listener.getEvents(), hasSize(3));
        assertThat(this.listener.getEvents().get(0), is(instanceOf(EyesOpenedEvent.class)));
        assertThat(this.listener.getEvents().get(1), is(instanceOf(EyesClosedEvent.class)));
        assertThat(this.listener.getEvents().get(2), is(instanceOf(EyesOpenedEvent.class)));
    }

    @Test
    public void shouldCreateSlowEyelidClosureEvents() {
        fail("not yet implemented");
    }

    @Test
    public void shouldCreateNormalEyeBlinkEvents() {
        fail("not yet implemented");
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD_MR1)
    private void detectorConsumesVideo(final int framesPerSecond, final int videoResource) {
        MediaMetadataRetriever retriever = this.createRetriever(videoResource);

        double incMicros = 1000000.0 / (double) framesPerSecond;
        long durationMicros = this.getDurationMillis(retriever) * 1000;
        for(long timeMicros = 0; timeMicros < durationMicros; timeMicros += incMicros) {
            Frame frame =
                    new Frame
                            .Builder()
                            .setBitmap(this.getFrameAtTime(retriever, timeMicros))
                            .setTimestampMillis(timeMicros / 1000)
                            .build();
            this.detector.receiveFrame(frame);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD_MR1)
    @NonNull
    private MediaMetadataRetriever createRetriever(final int videoResource) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(this.appContext, this.createUri(videoResource));
        return retriever;
    }

    private Uri createUri(final int resource) {
        return Uri.parse("android.resource://" + this.appContext.getPackageName() + ".test/" + resource);
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD_MR1)
    private long getDurationMillis(final MediaMetadataRetriever retriever) {
        return Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD_MR1)
    private Bitmap getFrameAtTime(final MediaMetadataRetriever retriever, final long timeMicros) {
        return retriever.getFrameAtTime(timeMicros, MediaMetadataRetriever.OPTION_CLOSEST);
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
