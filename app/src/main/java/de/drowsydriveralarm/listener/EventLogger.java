package de.drowsydriveralarm.listener;

import android.util.Log;

import com.google.common.eventbus.Subscribe;

import de.drowsydriveralarm.event.Event;
import de.drowsydriveralarm.event.UpdateEvent;

public class EventLogger {

    @Subscribe
    public void logEvent(final Event event) {
        if (event instanceof UpdateEvent) {
            return;
        }

        Log.d(this.getClass().getSimpleName(), "" + event);
    }
}
