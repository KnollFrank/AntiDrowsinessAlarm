package de.drowsydriveralarm.eventproducer;

import com.google.android.gms.vision.face.Face;
import com.google.common.base.Optional;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import de.drowsydriveralarm.event.EyesClosedEvent;
import de.drowsydriveralarm.event.EyesOpenedEvent;
import de.drowsydriveralarm.event.UpdateEvent;

public class EyesClosedEventProducer extends EventProducer {

    private final float eyeOpenProbabilityThreshold;
    private Optional<Boolean> previouslyEyesOpened = Optional.absent();

    public EyesClosedEventProducer(final float eyeOpenProbabilityThreshold, final EventBus eventBus) {
        super(eventBus);
        this.eyeOpenProbabilityThreshold = eyeOpenProbabilityThreshold;
    }

    @Subscribe
    public void onEyesOpenedEvent(EyesOpenedEvent event) {
        this.previouslyEyesOpened = Optional.of(true);
    }

    @Subscribe
    public void onUpdateEvent(final UpdateEvent actualEvent) {
        if (this.isPreviouslyEyesOpened() && this.isEyesClosed(actualEvent.getFace())) {
            this.previouslyEyesOpened = Optional.of(false);
            this.postEvent(new EyesClosedEvent(EyesOpenedEventProducer.getInstantOf(actualEvent)));
        }
    }

    private boolean isPreviouslyEyesOpened() {
        return !this.previouslyEyesOpened.isPresent() || this.previouslyEyesOpened.get();
    }

    // TODO: verwende face.getLandmarks(), um festzustellen, ob beide Augen als Landmarks erkannt wurden.
    // Falls nicht, ist der DrowsyDriverAlarm außer Betrieb zu setzen.
    private boolean isEyesClosed(final Face face) {
        return this.isDefined(face.getIsLeftEyeOpenProbability()) && face.getIsLeftEyeOpenProbability() < this.eyeOpenProbabilityThreshold &&
                this.isDefined(face.getIsRightEyeOpenProbability()) && face.getIsRightEyeOpenProbability() < this.eyeOpenProbabilityThreshold;
    }

    private boolean isDefined(float probability) {
        return probability != Face.UNCOMPUTED_PROBABILITY;
    }
}