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
package de.antidrowsinessalarm.camera;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import com.google.android.gms.common.images.Size;
import com.google.android.gms.vision.CameraSource;

import java.io.IOException;

public class CameraSourcePreview extends ViewGroup {
    private static final String TAG = "CameraSourcePreview";

    private final Context mContext;
    private final SurfaceView mSurfaceView;
    private boolean mStartRequested;
    private boolean mSurfaceAvailable;
    private CameraSource mCameraSource;

    private GraphicOverlay mOverlay;

    public CameraSourcePreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext=context;
        this.mStartRequested=false;
        this.mSurfaceAvailable=false;

        this.mSurfaceView=new SurfaceView(context);
        this.mSurfaceView.getHolder().addCallback(new SurfaceCallback());
        this.addView(this.mSurfaceView);
    }

    public void start(CameraSource cameraSource) throws IOException {
        if (cameraSource == null) {
            this.stop();
        }

        this.mCameraSource=cameraSource;

        if(this.mCameraSource != null) {
            this.mStartRequested=true;
            this.startIfReady();
        }
    }

    public void start(CameraSource cameraSource, GraphicOverlay overlay) throws IOException {
        this.mOverlay=overlay;
        this.start(cameraSource);
    }

    public void stop() {
        if(this.mCameraSource != null) {
            this.mCameraSource.stop();
        }
    }

    public void release() {
        if(this.mCameraSource != null) {
            this.mCameraSource.release();
            this.mCameraSource=null;
        }
    }

    private void startIfReady() throws IOException {
        if(this.mStartRequested && this.mSurfaceAvailable) {
            this.mCameraSource.start(this.mSurfaceView.getHolder());
            if(this.mOverlay != null) {
                Size size=this.mCameraSource.getPreviewSize();
                int min = Math.min(size.getWidth(), size.getHeight());
                int max = Math.max(size.getWidth(), size.getHeight());
                if(this.isPortraitMode()) {
                    // Swap width and height sizes when in portrait, since it will be rotated by
                    // 90 degrees
                    this.mOverlay.setCameraInfo(min, max, this.mCameraSource.getCameraFacing());
                } else {
                    this.mOverlay.setCameraInfo(max, min, this.mCameraSource.getCameraFacing());
                }
                this.mOverlay.clear();
            }
            this.mStartRequested=false;
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int width = 320;
        int height = 240;
        if(this.mCameraSource != null) {
            Size size=this.mCameraSource.getPreviewSize();
            if (size != null) {
                width = size.getWidth();
                height = size.getHeight();
            }
        }

        // Swap width and height sizes when in portrait, since it will be rotated 90 degrees
        if(this.isPortraitMode()) {
            int tmp = width;
            width = height;
            height = tmp;
        }

        final int layoutWidth = right - left;
        final int layoutHeight = bottom - top;

        // Computes height and width for potentially doing fit width.
        int childWidth = layoutWidth;
        int childHeight = (int)(((float) layoutWidth / (float) width) * height);

        // If height is too tall using fit width, does fit height instead.
        if (childHeight > layoutHeight) {
            childHeight = layoutHeight;
            childWidth = (int)(((float) layoutHeight / (float) height) * width);
        }

        for(int i=0; i < this.getChildCount(); ++i) {
            this.getChildAt(i).layout(0, 0, childWidth, childHeight);
        }

        try {
            this.startIfReady();
        } catch (IOException e) {
            Log.e(TAG, "Could not start camera source.", e);
        }
    }

    private boolean isPortraitMode() {
        int orientation=this.mContext.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return false;
        }
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            return true;
        }

        Log.d(TAG, "isPortraitMode returning false by default");
        return false;
    }

    private class SurfaceCallback implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder surface) {
            CameraSourcePreview.this.mSurfaceAvailable=true;
            try {
                CameraSourcePreview.this.startIfReady();
            } catch (IOException e) {
                Log.e(TAG, "Could not start camera source.", e);
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surface) {
            CameraSourcePreview.this.mSurfaceAvailable=false;
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }
    }
}
