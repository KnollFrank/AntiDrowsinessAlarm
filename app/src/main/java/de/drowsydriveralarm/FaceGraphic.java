/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.drowsydriveralarm;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;
import com.google.common.base.Function;
import com.google.common.base.Optional;

import de.drowsydriveralarm.camera.GraphicOverlay;

/**
 * Graphic instance for rendering face position, orientation, and landmarks within an associated
 * graphic overlay view.
 */
class FaceGraphic extends GraphicOverlay.Graphic {
    private static final float FACE_POSITION_RADIUS = 10.0f;
    private static final float ID_TEXT_SIZE = 40.0f;
    private static final float ID_Y_OFFSET = 50.0f;
    private static final float ID_X_OFFSET = -50.0f;
    private static final float BOX_STROKE_WIDTH = 5.0f;

    private static final int COLOR_CHOICES[] = {
            Color.BLUE,
            Color.CYAN,
            Color.GREEN,
            Color.MAGENTA,
            Color.RED,
            Color.WHITE,
            Color.YELLOW
    };
    private static int currentColorIndex = 0;

    private final Paint facePositionPaint;
    private final Paint idPaint;
    private final Paint boxPaint;
    private final Paint eyeOutlinePaint;

    private volatile Face face;
    private int faceId;

    FaceGraphic(final GraphicOverlay overlay) {
        super(overlay);

        currentColorIndex = (currentColorIndex + 1) % COLOR_CHOICES.length;
        final int selectedColor = COLOR_CHOICES[currentColorIndex];

        this.facePositionPaint = new Paint();
        this.facePositionPaint.setColor(selectedColor);

        this.idPaint = new Paint();
        this.idPaint.setColor(selectedColor);
        this.idPaint.setTextSize(ID_TEXT_SIZE);

        this.boxPaint = new Paint();
        this.boxPaint.setColor(selectedColor);
        this.boxPaint.setStyle(Paint.Style.STROKE);
        this.boxPaint.setStrokeWidth(BOX_STROKE_WIDTH);

        this.eyeOutlinePaint = new Paint();
        this.eyeOutlinePaint.setColor(Color.BLACK);
        this.eyeOutlinePaint.setStyle(Paint.Style.STROKE);
        this.eyeOutlinePaint.setStrokeWidth(5);
    }

    void setId(final int id) {
        this.faceId = id;
    }


    /**
     * Updates the face instance from the detection of the most recent frame.  Invalidates the
     * relevant portions of the overlay to trigger a redraw.
     */
    void updateFace(final Face face) {
        this.face = face;
        this.postInvalidate();
    }

    /**
     * Draws the face annotations for position on the supplied canvas.
     */
    @Override
    public void draw(final Canvas canvas) {
        final Face face = this.face;
        if (face == null) {
            return;
        }

        // Draws a circle at the position of the detected face, with the face's track id below.
        final float x = this.translateX(face.getPosition().x + face.getWidth() / 2);
        final float y = this.translateY(face.getPosition().y + face.getHeight() / 2);
        canvas.drawCircle(x, y, FACE_POSITION_RADIUS, this.facePositionPaint);
        canvas.drawText("id: " + this.faceId, x + ID_X_OFFSET, y + ID_Y_OFFSET, this.idPaint);
        this.drawEyesIfDetected(canvas, face);
        this.drawEyesOpenProbabilitiesIfDetected(canvas, face);

        // Draws a bounding box around the face.
        final float xOffset = this.scaleX(face.getWidth() / 2.0f);
        final float yOffset = this.scaleY(face.getHeight() / 2.0f);
        final float left = x - xOffset;
        final float top = y - yOffset;
        final float right = x + xOffset;
        final float bottom = y + yOffset;
        canvas.drawRect(left, top, right, bottom, this.boxPaint);
    }

    private void drawEyesOpenProbabilitiesIfDetected(final Canvas canvas, final Face face) {
        this.drawLeftEyeOpenProbabilityIfDetected(canvas, face);
        this.drawRightEyeOpenProbabilityIfDetected(canvas, face);
    }

    private void drawLeftEyeOpenProbabilityIfDetected(final Canvas canvas, final Face face) {
        this.drawEyeOpenProbabilityIfDetected(
                canvas,
                face,
                Landmark.LEFT_EYE,
                new Function<Face, Float>() {
                    @Override
                    public Float apply(final Face face) {
                        return face.getIsLeftEyeOpenProbability();
                    }
                });
    }

    private void drawRightEyeOpenProbabilityIfDetected(final Canvas canvas, final Face face) {
        this.drawEyeOpenProbabilityIfDetected(
                canvas,
                face,
                Landmark.RIGHT_EYE,
                new Function<Face, Float>() {
                    @Override
                    public Float apply(final Face face) {
                        return face.getIsRightEyeOpenProbability();
                    }
                });
    }

    private void drawEyeOpenProbabilityIfDetected(final Canvas canvas, final Face face, final int eyeLandmark, final Function<Face, Float> isEyeOpenProbabilitySupplier) {
        final Optional<PointF> eyePos = this.getLandmarkPosition(face, eyeLandmark);
        if (eyePos.isPresent()) {
            canvas.drawText(String.format("%.2f", isEyeOpenProbabilitySupplier.apply(face)), this.translateX(eyePos.get().x), this.translateY(eyePos.get().y), this.idPaint);
        }
    }

    private void drawEyesIfDetected(final Canvas canvas, final Face face) {
        this.drawEyeIfDetected(canvas, face, Landmark.LEFT_EYE);
        this.drawEyeIfDetected(canvas, face, Landmark.RIGHT_EYE);
    }

    private void drawEyeIfDetected(final Canvas canvas, final Face face, final int eyeLandmark) {
        final float eyeRadius = 50;
        final Optional<PointF> eyePos = this.getLandmarkPosition(face, eyeLandmark);
        if (eyePos.isPresent()) {
            final PointF eyePos2Draw = new PointF(this.translateX(eyePos.get().x), this.translateY(eyePos.get().y));
            canvas.drawCircle(eyePos2Draw.x, eyePos2Draw.y, eyeRadius, this.eyeOutlinePaint);
        }
    }

    /**
     * Finds a specific landmark position, or approximates the position based on past observations
     * if it is not present.
     */
    private Optional<PointF> getLandmarkPosition(final Face face, final int landmarkId) {
        for (final Landmark landmark : face.getLandmarks()) {
            if (landmark.getType() == landmarkId) {
                return Optional.of(landmark.getPosition());
            }
        }
        return Optional.absent();
    }
}
