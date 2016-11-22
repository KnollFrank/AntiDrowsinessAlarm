package de.antidrowsinessalarm;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.antidrowsinessalarm.eventproducer.DrowsyEventDetector;
import de.antidrowsinessalarm.test.R;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class EventTest {

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD_MR1)
    @Test
    public void test() {
        // Given
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("de.antidrowsinessalarm" +
                "", appContext.getPackageName());
        // TODO: move slow_eyelid_closure.mp4 and normal_eye_blink.mp4 to raw directory within androidTest
        Uri videoUri = Uri.parse("android.resource://" + appContext.getPackageName() + ".test/" + R.raw.slow_eyelid_closure);
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(appContext, videoUri);

        FaceDetector detector = new FaceDetector.Builder(appContext)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setMode(FaceDetector.ACCURATE_MODE)
                .build();

        detector.setProcessor(
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
                        .build());

        double inc = 1000000.0 / 30.0;
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInmillisec = Long.parseLong(time);
        long duration = timeInmillisec / 1000;
        long hours = duration / 3600;
        long minutes = (duration - hours * 3600) / 60;
        long seconds = duration - (hours * 3600 + minutes * 60);
        // When
        for(long i = 0; i < timeInmillisec * 1000; i += inc) {
            final Bitmap bitmap = retriever.getFrameAtTime(i, MediaMetadataRetriever.OPTION_CLOSEST);
            assertThat(bitmap, is(not(nullValue())));
            Frame frame = new Frame.Builder().setBitmap(bitmap).setTimestampMillis(i / 1000).build();
            detector.receiveFrame(frame);
        }

        // Then

    }

    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            return new DrowsyEventDetector().getGraphicFaceTracker();
        }
    }
}
