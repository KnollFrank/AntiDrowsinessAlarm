package de.antidrowsinessalarm.eventproducer;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.face.Face;
import com.google.common.base.Optional;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import de.antidrowsinessalarm.event.EyesClosedEvent;
import de.antidrowsinessalarm.event.EyesOpenedEvent;
import de.antidrowsinessalarm.event.UpdateEvent;

public class EyesOpenedEventProducer extends EventProducer {

    private Optional<Boolean> previouslyEyesClosed = Optional.absent();

    public EyesOpenedEventProducer(EventBus eventBus) {
        super(eventBus);
    }

    static boolean isEyesOpen(Face face) {
        // TODO: make 0.5 configurable
        return face.getIsLeftEyeOpenProbability() >= 0.5 && face.getIsRightEyeOpenProbability() >= 0.5;
    }

    @Subscribe
    public void onEyesClosedEvent(EyesClosedEvent event) {
        this.previouslyEyesClosed = Optional.of(true);
    }

    @Subscribe
    public void onUpdateEvent(final UpdateEvent actualEvent) {
        if(this.isPreviouslyEyesClosed() && isEyesOpen(actualEvent.getFace())) {
            this.previouslyEyesClosed = Optional.of(false);
            this.postEvent(new EyesOpenedEvent(this.getTimestampMillis(actualEvent.getDetections())));
        }
    }

    private boolean isPreviouslyEyesClosed() {
        return !this.previouslyEyesClosed.isPresent() || this.previouslyEyesClosed.get();
    }

    private long getTimestampMillis(Detector.Detections<Face> detections) {
        return detections.getFrameMetadata().getTimestampMillis();
    }
}
