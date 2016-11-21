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
package de.antidrowsinessalarm;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import de.antidrowsinessalarm.camera.GraphicOverlay;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;
import com.google.common.base.Function;
import com.google.common.base.Optional;

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
    private static int mCurrentColorIndex = 0;

    private final Paint mFacePositionPaint;
    private final Paint mIdPaint;
    private final Paint mBoxPaint;
    private final Paint mEyeOutlinePaint;

    private volatile Face mFace;
    private int mFaceId;
    private float mFaceHappiness;

    FaceGraphic(GraphicOverlay overlay) {
        super(overlay);

        mCurrentColorIndex = (mCurrentColorIndex + 1) % COLOR_CHOICES.length;
        final int selectedColor = COLOR_CHOICES[mCurrentColorIndex];

        this.mFacePositionPaint=new Paint();
        this.mFacePositionPaint.setColor(selectedColor);

        this.mIdPaint=new Paint();
        this.mIdPaint.setColor(selectedColor);
        this.mIdPaint.setTextSize(ID_TEXT_SIZE);

        this.mBoxPaint=new Paint();
        this.mBoxPaint.setColor(selectedColor);
        this.mBoxPaint.setStyle(Paint.Style.STROKE);
        this.mBoxPaint.setStrokeWidth(BOX_STROKE_WIDTH);

        this.mEyeOutlinePaint=new Paint();
        this.mEyeOutlinePaint.setColor(Color.BLACK);
        this.mEyeOutlinePaint.setStyle(Paint.Style.STROKE);
        this.mEyeOutlinePaint.setStrokeWidth(5);
    }

    void setId(int id) {
        this.mFaceId=id;
    }


    /**
     * Updates the face instance from the detection of the most recent frame.  Invalidates the
     * relevant portions of the overlay to trigger a redraw.
     */
    void updateFace(Face face) {
        this.mFace=face;
        this.postInvalidate();
    }

    /**
     * Draws the face annotations for position on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {
        Face face=this.mFace;
        if (face == null) {
            return;
        }

        // Draws a circle at the position of the detected face, with the face's track id below.
        float x=this.translateX(face.getPosition().x + face.getWidth() / 2);
        float y=this.translateY(face.getPosition().y + face.getHeight() / 2);
        canvas.drawCircle(x, y, FACE_POSITION_RADIUS, this.mFacePositionPaint);
        canvas.drawText("id: " + this.mFaceId, x + ID_X_OFFSET, y + ID_Y_OFFSET, this.mIdPaint);
        this.drawEyesIfDetected(canvas, face);
        this.drawEyesOpenProbabilitiesIfDetected(canvas, face);

        // Draws a bounding box around the face.
        float xOffset=this.scaleX(face.getWidth() / 2.0f);
        float yOffset=this.scaleY(face.getHeight() / 2.0f);
        float left = x - xOffset;
        float top = y - yOffset;
        float right = x + xOffset;
        float bottom = y + yOffset;
        canvas.drawRect(left, top, right, bottom, this.mBoxPaint);
    }

    private void drawEyesOpenProbabilitiesIfDetected(Canvas canvas, Face face) {
        this.drawLeftEyeOpenProbabilityIfDetected(canvas, face);
        this.drawRightEyeOpenProbabilityIfDetected(canvas, face);
    }

    private void drawLeftEyeOpenProbabilityIfDetected(Canvas canvas, Face face) {
        this.drawEyeOpenProbabilityIfDetected(
                canvas,
                face,
                Landmark.LEFT_EYE,
                new Function<Face, Float>() {
                    @Override
                    public Float apply(Face face) {
                        return face.getIsLeftEyeOpenProbability();
                    }
                });
    }

    private void drawRightEyeOpenProbabilityIfDetected(Canvas canvas, Face face) {
        this.drawEyeOpenProbabilityIfDetected(
                canvas,
                face,
                Landmark.RIGHT_EYE,
                new Function<Face, Float>() {
                    @Override
                    public Float apply(Face face) {
                        return face.getIsRightEyeOpenProbability();
                    }
                });
    }

    private void drawEyeOpenProbabilityIfDetected(Canvas canvas, Face face, int eyeLandmark, Function<Face, Float> isEyeOpenProbabilitySupplier) {
        Optional<PointF> eyePos=this.getLandmarkPosition(face, eyeLandmark);
        if(eyePos.isPresent()) {
            canvas.drawText(String.format("%.2f", isEyeOpenProbabilitySupplier.apply(face)), this.translateX(eyePos.get().x), this.translateY(eyePos.get().y), this.mIdPaint);
        }
    }

    private void drawEyesIfDetected(Canvas canvas, Face face) {
        this.drawEyeIfDetected(canvas, face, Landmark.LEFT_EYE);
        this.drawEyeIfDetected(canvas, face, Landmark.RIGHT_EYE);
    }

    private void drawEyeIfDetected(Canvas canvas, Face face, int eyeLandmark) {
        float eyeRadius = 50;
        Optional<PointF> eyePos=this.getLandmarkPosition(face, eyeLandmark);
        if(eyePos.isPresent()) {
            PointF eyePos2Draw=new PointF(this.translateX(eyePos.get().x), this.translateY(eyePos.get().y));
            canvas.drawCircle(this.translateX(eyePos.get().x), eyePos2Draw.y, eyeRadius, this.mEyeOutlinePaint);
        }
    }

    /**
     * Finds a specific landmark position, or approximates the position based on past observations
     * if it is not present.
     */
    private Optional<PointF> getLandmarkPosition(Face face, int landmarkId) {
        for (Landmark landmark : face.getLandmarks()) {
            if (landmark.getType() == landmarkId) {
                return Optional.of(landmark.getPosition());
            }
        }
        return Optional.absent();
    }
}
