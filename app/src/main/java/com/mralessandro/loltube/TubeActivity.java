package com.mralessandro.loltube;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

enum RotationState {
    INITIAL,
    UPSIDE_UP,
    UPSIDE_DOWN
}

public class TubeActivity extends AppCompatActivity implements SensorEventListener, Enableable {
    private static final String TAG = "TubeActivity";
    private static final float ROTATION_THRESHOLD = (float) 1.0;
    private SamplesBuffer buffer;
    private Context context;
    private RotationState rotationState;
    private SensorManager sensorManager;
    private Sensor acelerationSensor;
    private AudioManager audioManager;
    private SoundsRandomPlayer soundsRandomPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this.getApplicationContext();
        this.rotationState = RotationState.INITIAL;
        this.audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        this.acelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.soundsRandomPlayer = new SoundsRandomPlayer(context, this);
        this.buffer = new SamplesBuffer();
        setContentView(R.layout.activity_tube);
    }

    @Override
    public void enable() {
        this.sensorManager.registerListener(this, this.acelerationSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void disable() {
        this.sensorManager.unregisterListener(this, this.acelerationSensor);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(context, R.string.tilt_device_message, Toast.LENGTH_SHORT).show();
        this.enable();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.disable();
    }

    protected void onDestroy() {
        super.onDestroy();
        this.soundsRandomPlayer.destroy();
    }

    private void activate() {
        // Volume check
        int currentVolume = this.audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        // Show toast if volume is 0 and return
        if (currentVolume <= 0) {
            Toast.makeText(this.context, R.string.turn_up_volume_message, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Play sound and set rotationState to TO_RESET
            this.disable();
            this.soundsRandomPlayer.playRandom();
        } catch (IOException e) {
            Toast.makeText(this.context, R.string.IO_error, Toast.LENGTH_SHORT).show();
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;

        // Check if event comes from accelerometer
        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // Get Y-axis acceleration value
            this.buffer.sample(event.values[1]);

            // Check rotationState
            switch (this.rotationState) {
                case UPSIDE_UP:
                    if (this.buffer.getAverage() <= ROTATION_THRESHOLD * -1) {
                        this.activate();
                        this.rotationState = RotationState.UPSIDE_DOWN;
                    }
                    break;
                case UPSIDE_DOWN:
                    if (this.buffer.getAverage() >= ROTATION_THRESHOLD) {
                        this.activate();
                        this.rotationState = RotationState.UPSIDE_UP;
                    }
                    break;
                case INITIAL:
                    if (event.values[1] <= 0)
                        this.rotationState = RotationState.UPSIDE_DOWN;
                    else
                        this.rotationState = RotationState.UPSIDE_UP;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}