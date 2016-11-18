package com.google.android.gms.samples.vision.face.facetracker.listener;

import com.google.android.gms.samples.vision.face.facetracker.event.EyesClosedEvent;
import com.google.android.gms.samples.vision.face.facetracker.event.UpdateEvent;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.face.Face;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class EyesClosedEventProducer extends EventProducer {

    private Boolean isEyesClosed;

    public EyesClosedEventProducer(EventBus eventBus) {
        super(eventBus);
    }

    @Subscribe
    public void maybeProduceEyesClosedEvent(UpdateEvent event) {
        if(this.isEyesClosed(event.getFace())) {
            if(this.isEyesClosed == null || !this.isEyesClosed) {
                this.eventBus.post(new EyesClosedEvent(this.getTimestampMillis(event.getDetections())));
                this.isEyesClosed = true;
            }
        }
    }

    private boolean isEyesClosed(Face face) {
        return face.getIsLeftEyeOpenProbability() < 0.5 && face.getIsRightEyeOpenProbability() < 0.5;
    }

    private long getTimestampMillis(Detector.Detections<Face> detections) {
        return detections.getFrameMetadata().getTimestampMillis();
    }
}
