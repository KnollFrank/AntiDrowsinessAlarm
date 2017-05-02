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

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import de.drowsydriveralarm.camera.CameraSourcePreview;
import de.drowsydriveralarm.camera.GraphicOverlay;

// FIXME: manchmal ist die Anwendung in Betrieb während eines Hupens, aber trotzdem kam kein AppActiveEvent, der grün anzeigen würde.
// TODO: die Anwendung soll sich in den Hintegrund schalten können, und sich im Falle von erkannter Schläfrigkeit des Fahrers in den Vordergrund schalten können, bzw. lediglich einen Alarmton abgeben und ein rotes Signal über der gerade aktiven Anwendung einblenden.
// TODO: die Ampeln in angemessener Größe darstellen, z.B. 1/3 der verfügbaren Bildschirmhöhe.
// TODO: innerhalb der Active/Idle-Ampel ein Bauarbeitersymbol transparent darstellen und ein Label der Ampel voranstellen.
// TODO: innerhalb der Drowsy/Likelydrowsy/Awake-Ampel ein waches Gesicht, schläfriges Gesicht und ein schlafendes Gesicht einblenden.
public final class FaceTrackerActivity extends AppCompatActivity {

    private static final String TAG = "FaceTrackerActivity";

    private CameraSourcePreview preview;
    private GraphicOverlay graphicOverlay;
    private TextView eyesInfoView;
    private ImageView imageView;
    private ImageView appActiveIdleView;
    private CameraSourceHandler cameraSourceHandler;
    private CameraPermissionHandler cameraPermissionHandler;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main);
        final Toolbar myToolbar = (Toolbar) this.findViewById(R.id.my_toolbar);
        this.setSupportActionBar(myToolbar);

        this.preview = (CameraSourcePreview) this.findViewById(R.id.preview);
        this.graphicOverlay = (GraphicOverlay) this.findViewById(R.id.faceOverlay);
        this.eyesInfoView = (TextView) this.findViewById(R.id.eyesInfoView);
        this.imageView = (ImageView) this.findViewById(R.id.imageView);
        this.appActiveIdleView = (ImageView) this.findViewById(R.id.appActiveIdleView);
        this.cameraSourceHandler = new CameraSourceHandler(this);
        this.cameraPermissionHandler = new CameraPermissionHandler(this, this.cameraSourceHandler);
        this.cameraPermissionHandler.createCameraSourceOrRequestCameraPermission();
    }

    public GraphicOverlay getGraphicOverlay() {
        return this.graphicOverlay;
    }

    public TextView getEyesInfoView() {
        return this.eyesInfoView;
    }

    public ImageView getImageView() {
        return this.imageView;
    }

    public ImageView getAppActiveIdleView() {
        return this.appActiveIdleView;
    }

    public CameraSourcePreview getPreview() {
        return this.preview;
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        this.getMenuInflater().inflate(R.menu.toolbarmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                this.startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.cameraSourceHandler.releaseCameraSource();
        this.cameraSourceHandler.createCameraSource();
        this.cameraSourceHandler.startCameraSource();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.preview.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.cameraSourceHandler.releaseCameraSource();
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        this.cameraPermissionHandler.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
