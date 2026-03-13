package com.memoria.kiosk.monitor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SensorMonitor implements SensorEventListener {

    private final SensorManager sensorManager;
    private final Sensor accelerometer;
    private final Consumer<String> callback;
    private final List<Float> magnitudeWindow = new ArrayList<>();
    private final Handler handler = new Handler(Looper.getMainLooper());

    private final Runnable summaryTick = new Runnable() {
        @Override
        public void run() {
            callback.accept(buildSummary());
            handler.postDelayed(this, 5000L);
        }
    };

    public SensorMonitor(Context context, Consumer<String> callback) {
        this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        this.accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.callback = callback;
    }

    public void start() {
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
            handler.post(summaryTick);
        }
    }

    public void stop() {
        sensorManager.unregisterListener(this);
        handler.removeCallbacks(summaryTick);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        float magnitude = (float) Math.sqrt(x * x + y * y + z * z);

        magnitudeWindow.add(magnitude);
        if (magnitudeWindow.size() > 100) {
            magnitudeWindow.remove(0);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // no-op
    }

    private String buildSummary() {
        if (magnitudeWindow.isEmpty()) {
            return "collecting sensor data";
        }

        float mean = 0f;
        for (Float v : magnitudeWindow) {
            mean += v;
        }
        mean /= magnitudeWindow.size();

        float variance = 0f;
        for (Float v : magnitudeWindow) {
            float delta = v - mean;
            variance += delta * delta;
        }
        variance /= magnitudeWindow.size();

        return "window=" + magnitudeWindow.size() + " var=" + String.format("%.3f", variance);
    }
}
