package de.drowsydriveralarm.eventproducer;

import android.support.annotation.NonNull;

import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;

import java.util.Arrays;
import java.util.List;

class BothEyesRecognizedPredicate {

    public static boolean areBothEyesRecognized(final Face face) {
        return getLandmarkTypes(face.getLandmarks()).containsAll(getBothEyes());
    }

    private static ImmutableList<Integer> getLandmarkTypes(final List<Landmark> landmarks) {
        return FluentIterable
                .from(landmarks)
                .transform(
                        new Function<Landmark, Integer>() {

                            @Override
                            public Integer apply(final Landmark landmark) {
                                return landmark.getType();
                            }
                        })
                .toList();
    }

    @NonNull
    private static List<Integer> getBothEyes() {
        return Arrays.asList(Landmark.LEFT_EYE, Landmark.RIGHT_EYE);
    }
}
