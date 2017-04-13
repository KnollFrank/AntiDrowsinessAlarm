package de.drowsydriveralarm;

import org.joda.time.Instant;

public interface Clock {

    Instant now();
}
