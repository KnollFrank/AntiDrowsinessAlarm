package com.google.android.gms.samples.vision.face.facetracker.event;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class EyesOpenedEvent extends Event {

    public EyesOpenedEvent(final long timestamp) {
        super(timestamp);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    protected MoreObjects.ToStringHelper getToStringHelper() {
        return super.getToStringHelper();
    }
}
