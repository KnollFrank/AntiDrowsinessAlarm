package de.antidrowsinessalarm.event;

public class SlowEyelidClosureEvent extends DurationEvent {

    public SlowEyelidClosureEvent(final long timestampMillis, final long durationMillis) {
        super(timestampMillis, durationMillis);
    }
}
