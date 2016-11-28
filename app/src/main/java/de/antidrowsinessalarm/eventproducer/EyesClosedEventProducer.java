package de.antidrowsinessalarm.eventproducer;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.face.Face;
import com.google.common.base.Optional;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import de.antidrowsinessalarm.event.EyesClosedEvent;
import de.antidrowsinessalarm.event.EyesOpenedEvent;
import de.antidrowsinessalarm.event.UpdateEvent;

public class EyesClosedEventProducer extends EventProducer {

    private Optional<Boolean> previouslyEyesOpened = Optional.absent();

    public EyesClosedEventProducer(final EventBus eventBus) {
        super(eventBus);
    }

    @Subscribe
    public void onEyesOpenedEvent(EyesOpenedEvent event) {
        this.previouslyEyesOpened = Optional.of(true);
    }

    @Subscribe
    public void onUpdateEvent(final UpdateEvent actualEvent) {
        if(this.isPreviouslyEyesOpened() && this.isEyesClosed(actualEvent.getFace())) {
            this.previouslyEyesOpened = Optional.of(false);
            this.postEvent(new EyesClosedEvent(EyesOpenedEventProducer.getInstantOf(actualEvent)));
        }
    }

    private boolean isPreviouslyEyesOpened() {
        return !this.previouslyEyesOpened.isPresent() || this.previouslyEyesOpened.get();
    }

    private long getTimestampMillis(Detector.Detections<Face> detections) {
        return detections.getFrameMetadata().getTimestampMillis();
    }

    private boolean isEyesClosed(final Face face) {
        // TODO: make 0.5 configurable
        return this.isDefined(face.getIsLeftEyeOpenProbability()) && face.getIsLeftEyeOpenProbability() < 0.5 &&
                this.isDefined(face.getIsRightEyeOpenProbability()) && face.getIsRightEyeOpenProbability() < 0.5 ;
    }

    private boolean isDefined(float probability) {
        return probability != Face.UNCOMPUTED_PROBABILITY;
    }
}