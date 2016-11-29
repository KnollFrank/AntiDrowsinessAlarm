package de.antidrowsinessalarm;

import android.support.annotation.NonNull;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import de.antidrowsinessalarm.event.Event;

public class EventListener {

    private final List<Event> events = new ArrayList<Event>();

    @Subscribe
    public void recordEvent(final Event event) {
        this.events.add(event);
    }

    public Event getEvent() {
        return !this.events.isEmpty() ? this.events.get(this.events.size() - 1) : null;
    }

    public List<Event> getEvents() {
        return this.events;
    }

    public <T> List<T> filterEventsBy(final Class<T> clazz) {
        return this.getListenerEvents().filter(clazz).toList();
    }

    public List<Event> filterEventsBy(final Class... eventClasses) {
        return this
                .getListenerEvents()
                .filter(this.isListenerEventClassContainedIn(eventClasses))
                .toList();
    }

    @NonNull
    private FluentIterable<Event> getListenerEvents() {
        return FluentIterable.from(this.events);
    }

    @NonNull
    private Predicate<Event> isListenerEventClassContainedIn(final Class[] eventClasses) {
        return new Predicate<Event>() {

            @Override
            public boolean apply(final Event listenerEvent) {
                return FluentIterable
                        .from(eventClasses)
                        .anyMatch(this.hasSameClassAs(listenerEvent.getClass()));
            }

            @NonNull
            private Predicate<Class> hasSameClassAs(final Class listenerEventClass) {
                return new Predicate<Class>() {

                    @Override
                    public boolean apply(final Class eventClass) {
                        return eventClass.equals(listenerEventClass);
                    }
                };
            }
        };
    }
}
