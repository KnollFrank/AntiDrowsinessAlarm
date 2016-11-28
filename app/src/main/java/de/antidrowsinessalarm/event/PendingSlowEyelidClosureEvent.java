package de.antidrowsinessalarm.event;

public class PendingSlowEyelidClosureEvent extends SlowEyelidClosureEvent {

    public PendingSlowEyelidClosureEvent(final long timestampMillis, final long durationMillis) {
        super(timestampMillis, durationMillis);
    }
}
