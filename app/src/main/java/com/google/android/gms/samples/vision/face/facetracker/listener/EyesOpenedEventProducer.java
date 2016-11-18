package com.google.android.gms.samples.vision.face.facetracker.listener;

import com.google.android.gms.samples.vision.face.facetracker.event.EyesOpenedEvent;
import com.google.android.gms.samples.vision.face.facetracker.event.UpdateEvent;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.face.Face;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class EyesOpenedEventProducer extends EventProducer {

    private Boolean isEyesOpen;

    public EyesOpenedEventProducer(EventBus eventBus) {
        super(eventBus);
    }

    @Subscribe
    public void maybeProduceEyesOpenedEvent(UpdateEvent event) {
        if(this.isEyesOpen(event.getFace())) {
            if(this.isEyesOpen == null || !this.isEyesOpen) {
                this.eventBus.post(new EyesOpenedEvent(this.getTimestampMillis(event.getDetections())));
                this.isEyesOpen = true;
            }
        }
    }

    private boolean isEyesOpen(Face face) {
        return face.getIsLeftEyeOpenProbability() >= 0.5 && face.getIsRightEyeOpenProbability() >= 0.5;
    }

    private long getTimestampMillis(Detector.Detections<Face> detections) {
        return detections.getFrameMetadata().getTimestampMillis();
    }
}
