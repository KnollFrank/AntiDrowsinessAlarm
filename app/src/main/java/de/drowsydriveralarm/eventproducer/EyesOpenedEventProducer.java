package de.drowsydriveralarm.eventproducer;

import com.google.android.gms.vision.face.Face;
import com.google.common.base.Optional;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import org.joda.time.Instant;

import de.drowsydriveralarm.event.EyesClosedEvent;
import de.drowsydriveralarm.event.EyesOpenedEvent;
import de.drowsydriveralarm.event.UpdateEvent;

public class EyesOpenedEventProducer extends EventProducer {

    private final float eyeOpenProbabilityThreshold;
    private Optional<Boolean> previouslyEyesClosed = Optional.absent();

    public EyesOpenedEventProducer(final float eyeOpenProbabilityThreshold, final EventBus eventBus) {
        super(eventBus);
        this.eyeOpenProbabilityThreshold = eyeOpenProbabilityThreshold;
    }

    static boolean isEyesOpen(Face face, final float eyeOpenProbabilityThreshold) {
        return face.getIsLeftEyeOpenProbability() >= eyeOpenProbabilityThreshold && face.getIsRightEyeOpenProbability() >= eyeOpenProbabilityThreshold;
    }

    // TODO: move to EventHelper class
    static Instant getInstantOf(UpdateEvent event) {
        return new Instant(event.getDetections().getFrameMetadata().getTimestampMillis());
    }

    @Subscribe
    public void onEyesClosedEvent(EyesClosedEvent event) {
        this.previouslyEyesClosed = Optional.of(true);
    }

    @Subscribe
    public void onUpdateEvent(final UpdateEvent actualEvent) {
        if (this.isPreviouslyEyesClosed() && isEyesOpen(actualEvent.getFace(), this.eyeOpenProbabilityThreshold)) {
            this.previouslyEyesClosed = Optional.of(false);
            this.postEvent(new EyesOpenedEvent(getInstantOf(actualEvent)));
        }
    }

    private boolean isPreviouslyEyesClosed() {
        return !this.previouslyEyesClosed.isPresent() || this.previouslyEyesClosed.get();
    }
}