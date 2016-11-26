package de.antidrowsinessalarm.eventproducer;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.face.Face;
import com.google.common.eventbus.EventBus;

import de.antidrowsinessalarm.event.EyesClosedEvent;
import de.antidrowsinessalarm.event.UpdateEvent;

public class EyesClosedEventProducer extends StateChangeEventProducer {

    public EyesClosedEventProducer(final EventBus eventBus) {
        super(eventBus);
    }

    @Override
    protected boolean getState(final Face face) {
        return this.isEyesClosed(face);
    }

    private boolean isEyesClosed(final Face face) {
        // TODO: make 0.5 configurable
        return this.isDefined(face.getIsLeftEyeOpenProbability()) && face.getIsLeftEyeOpenProbability() < 0.5 &&
                this.isDefined(face.getIsRightEyeOpenProbability()) && face.getIsRightEyeOpenProbability() < 0.5 ;
    }

    private boolean isDefined(float probability) {
        return probability != Face.UNCOMPUTED_PROBABILITY;
    }

    @Override
    protected Object createStateChangeEventFrom(final UpdateEvent event) {
        return new EyesClosedEvent(this.getTimestampMillis(event.getDetections()));
    }

    private long getTimestampMillis(final Detector.Detections<Face> detections) {
        return detections.getFrameMetadata().getTimestampMillis();
    }
}
