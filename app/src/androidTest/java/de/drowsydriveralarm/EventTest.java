package de.drowsydriveralarm;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import org.joda.time.Instant;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;
import java.util.Set;

import de.drowsydriveralarm.event.AwakeEvent;
import de.drowsydriveralarm.event.DrowsyEvent;
import de.drowsydriveralarm.event.EyesClosedEvent;
import de.drowsydriveralarm.event.EyesOpenedEvent;
import de.drowsydriveralarm.event.LikelyDrowsyEvent;
import de.drowsydriveralarm.event.NormalEyeBlinkEvent;
import de.drowsydriveralarm.event.SlowEyelidClosureEvent;
import de.drowsydriveralarm.eventproducer.DrowsyEventDetector;
import de.drowsydriveralarm.eventproducer.DrowsyEventDetectorConfig;
import de.drowsydriveralarm.eventproducer.TestingDrowsyEventDetectorConfig;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.core.Is.isA;
import static org.hamcrest.core.IsNot.not;

@RunWith(AndroidJUnit4.class)
public class EventTest {

    private Context appContext;
    private EventListener eventListener;
    private DrowsyEventDetector drowsyEventDetector;
    private FaceDetector detector;

    @Before
    public void setup() {
        this.setup(new SystemClock());
    }

    private void setup(final Clock clock) {
        this.appContext = InstrumentationRegistry.getTargetContext();
        final TestingDrowsyEventDetectorConfig configFactory = new TestingDrowsyEventDetectorConfig(this.createSharedPreferences());
        this.drowsyEventDetector =
                new DrowsyEventDetector(
                        DrowsyEventDetectorConfig
                                .builder()
                                .withEyeOpenProbabilityThreshold(configFactory.getEyeOpenProbabilityThreshold())
                                .withConfig(configFactory.getConfig())
                                .withSlowEyelidClosureMinDuration(configFactory.getSlowEyelidClosureMinDuration())
                                .withTimeWindow(configFactory.getTimeWindow())
                                .build(),
                        true,
                        clock);
        this.eventListener = new EventListener();
        this.drowsyEventDetector.getEventBus().register(this.eventListener);
        this.detector = FaceDetectorFactory.createFaceDetector(this.appContext);
        // using LargestFaceFocusingProcessor instead of MultiProcessor like in production code leads to an exception on device while running test
        this.detector.setProcessor(new MultiProcessor.Builder<>(this.createFactory()).build());
    }

    @NonNull
    private SharedPreferences createSharedPreferences() {
        return new SharedPreferences() {
            @Override
            public Map<String, ?> getAll() {
                return null;
            }

            @Nullable
            @Override
            public String getString(final String key, @Nullable final String defValue) {
                switch (key) {
                    case "drowsyThreshold":
                        return "0.15";
                    case "slowEyelidClosureMinDuration":
                        return "500";
                    case "eyeOpenProbabilityThreshold":
                        return "0.5";
                    case "likelyDrowsyThreshold":
                        return "0.08";
                    case "timeWindow":
                        return "15000";
                }
                return null;
            }

            @Nullable
            @Override
            public Set<String> getStringSet(final String key, @Nullable final Set<String> defValues) {
                return null;
            }

            @Override
            public int getInt(final String key, final int defValue) {
                return 0;
            }

            @Override
            public long getLong(final String key, final long defValue) {
                return 0;
            }

            @Override
            public float getFloat(final String key, final float defValue) {
                return 0;
            }

            @Override
            public boolean getBoolean(final String key, final boolean defValue) {
                return false;
            }

            @Override
            public boolean contains(final String key) {
                return false;
            }

            @Override
            public Editor edit() {
                return null;
            }

            @Override
            public void registerOnSharedPreferenceChangeListener(final OnSharedPreferenceChangeListener listener) {
            }

            @Override
            public void unregisterOnSharedPreferenceChangeListener(final OnSharedPreferenceChangeListener listener) {
            }
        };
    }

    @Test
    public void testOpen() {
        // When
        this.detectorReceivesImage(R.drawable.eyes_opened, 0);

        // Thent
        assertThat(this.eventListener.filterEventsBy(EyesOpenedEvent.class), contains(instanceOf(EyesOpenedEvent.class)));
    }

    @Test
    public void testClose() {
        // When
        this.detectorReceivesImage(R.drawable.eyes_closed, 0);

        // Then
        assertThat(this.eventListener.filterEventsBy(EyesClosedEvent.class), contains(instanceOf(EyesClosedEvent.class)));
    }

    @Test
    public void testOpenClose() {
        // When
        this.detectorReceivesImage(R.drawable.eyes_opened, 0);
        this.detectorReceivesImage(R.drawable.eyes_closed, 1000);

        // Then
        assertThat(
                this.eventListener.filterEventsBy(EyesOpenedEvent.class, EyesClosedEvent.class),
                contains(
                        instanceOf(EyesOpenedEvent.class),
                        instanceOf(EyesClosedEvent.class)));
    }

    @Test
    public void testOpenCloseOpen() {
        // When
        this.detectorReceivesImage(R.drawable.eyes_opened, 0);
        this.detectorReceivesImage(R.drawable.eyes_closed, 1000);
        this.detectorReceivesImage(R.drawable.eyes_opened, 2000);

        // Then
        assertThat(
                this.eventListener.filterEventsBy(EyesOpenedEvent.class, EyesClosedEvent.class),
                contains(
                        instanceOf(EyesOpenedEvent.class),
                        instanceOf(EyesClosedEvent.class),
                        instanceOf(EyesOpenedEvent.class)));
    }

    @Test
    public void shouldCreateSlowEyelidClosureEvent() {
        // When
        this.detectorReceivesImage(R.drawable.eyes_closed, 0);
        this.detectorReceivesImage(R.drawable.eyes_opened, 501);

        // Then
        assertThat(this.eventListener.filterEventsBy(SlowEyelidClosureEvent.class), contains(instanceOf(SlowEyelidClosureEvent.class)));
    }

    @Test
    public void shouldCreateNormalEyeBlinkEvent() {
        // When
        this.detectorReceivesImage(R.drawable.eyes_closed, 0);
        this.detectorReceivesImage(R.drawable.eyes_opened, 499);

        // Then
        assertThat(this.eventListener.filterEventsBy(NormalEyeBlinkEvent.class), contains(instanceOf(NormalEyeBlinkEvent.class)));
    }

    @Test
    public void shouldCreateSlowEyelidClosureEventAndNormalEyeBlinkEvent() {
        // When
        this.detectorReceivesImage(R.drawable.eyes_closed, 0);
        this.detectorReceivesImage(R.drawable.eyes_opened, 501);
        this.detectorReceivesImage(R.drawable.eyes_closed, 600);
        this.detectorReceivesImage(R.drawable.eyes_opened, 600 + 499);

        // Then
        assertThat(
                this.eventListener.filterEventsBy(SlowEyelidClosureEvent.class, NormalEyeBlinkEvent.class),
                contains(
                        instanceOf(SlowEyelidClosureEvent.class),
                        instanceOf(NormalEyeBlinkEvent.class)));
    }

    @Test
    public void shouldCreateDrowsyEvent() {
        // Given
        final MockedClock clock = new MockedClock();
        this.setup(clock);

        // When
        clock.setNow(new Instant(0));
        this.detectorReceivesImage(R.drawable.eyes_closed, 0);

        clock.setNow(new Instant(5000));
        this.detectorReceivesImage(R.drawable.eyes_opened, 5000);

        // Then
        assertThat(this.eventListener.getEvents(), hasItem(isA(DrowsyEvent.class)));
    }

    @Test
    public void shouldCreateDrowsyEventForEyesClosedTheWholeTime() {
        // Given
        final MockedClock clock = new MockedClock();
        this.setup(clock);

        // When
        clock.setNow(new Instant(0));
        this.detectorReceivesImage(R.drawable.eyes_closed, 0);

        clock.setNow(new Instant(15000));
        this.detectorReceivesImage(R.drawable.eyes_closed, 15000);

        // Then
        final double perclos = 1.0; // > 0.15
        assertThat(this.eventListener.getEvents(), hasItem(new DrowsyEvent(new Instant(15000), perclos)));
    }

    @Test
    public void shouldCreateDrowsyEventForEyesClosedButNotYetOpenedWhenMaybeProducingDrowsyEvent() {
        // Given
        final MockedClock clock = new MockedClock();
        this.setup(clock);

        // When
        clock.setNow(new Instant(0));
        this.detectorReceivesImage(R.drawable.eyes_closed, 0);

        clock.setNow(new Instant(501));
        this.detectorReceivesImage(R.drawable.eyes_opened, 501);

        clock.setNow(new Instant(510));
        this.detectorReceivesImage(R.drawable.eyes_closed, 510);

        clock.setNow(new Instant(15000));
        this.detectorReceivesImage(R.drawable.eyes_closed, 15000);

        // Then
        final double perclos = (501.0 + (15000.0 - 510.0)) / 15000.0; // = 0.9994 > 0.15
        assertThat(this.eventListener.getEvents(), hasItem(new DrowsyEvent(new Instant(15000), perclos)));
    }

    @Test
    public void shouldCreateNoDrowsyEvent() {
        // Given
        final MockedClock clock = new MockedClock();
        this.setup(clock);

        // When
        clock.setNow(new Instant(0));
        this.detectorReceivesImage(R.drawable.eyes_closed, 0);

        clock.setNow(new Instant(499));
        this.detectorReceivesImage(R.drawable.eyes_opened, 499);

        clock.setNow(new Instant(14000));
        this.detectorReceivesImage(R.drawable.eyes_closed_opened, 14000);

        // Then
        final double perclos = 0.0;
        assertThat(this.eventListener.getEvents(), not(hasItem(isA(DrowsyEvent.class))));
    }

    @Test
    public void shouldCreateLikelyDrowsyEvent() {
        // Given
        final MockedClock clock = new MockedClock();
        this.setup(clock);

        // When
        clock.setNow(new Instant(0));
        this.detectorReceivesImage(R.drawable.eyes_closed, 0);

        clock.setNow(new Instant(1500));
        this.detectorReceivesImage(R.drawable.eyes_opened, 1500);

        clock.setNow(new Instant(1501));
        // dummy image in order to give DrowsyEventProducer a chance to produce a DrowsyEvent
        this.detectorReceivesImage(R.drawable.eyes_closed, 1501);

        // Then
        assertThat(this.eventListener.getEvents(), hasItem(isA(LikelyDrowsyEvent.class)));
    }

    @Test
    public void shouldCreateAwakeEvent() {
        // Given
        final MockedClock clock = new MockedClock();
        this.setup(clock);

        // When
        clock.setNow(new Instant(0));
        this.detectorReceivesImage(R.drawable.eyes_opened, 0);

        // Then
        assertThat(this.eventListener.getEvents(), hasItem(isA(AwakeEvent.class)));
    }

    @Test
    public void shouldNotLooseEventsWhenLoosingFace() {
        // When
        this.detectorReceivesImage(R.drawable.eyes_opened, 0);
        this.detectorReceivesImage(R.drawable.face_not_visible, 1000);
        this.detectorReceivesImage(R.drawable.eyes_closed, 2000);

        // Then
        assertThat(
                this.eventListener.filterEventsBy(EyesOpenedEvent.class, EyesClosedEvent.class),
                contains(
                        instanceOf(EyesOpenedEvent.class),
                        instanceOf(EyesClosedEvent.class)));
    }

    private void detectorReceivesImage(final int imageResource, final long millis) {
        this.detector.receiveFrame(this.createFrame(imageResource, millis));
    }

    private Frame createFrame(final int imageResource, final long millis) {
        final Bitmap bitmap = this.getBitmap(imageResource);
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
                return EventTest.this.drowsyEventDetector.getEventProducingGraphicFaceTracker();
            }
        };
    }
}
