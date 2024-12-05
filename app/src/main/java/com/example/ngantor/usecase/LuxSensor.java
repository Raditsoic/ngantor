package com.example.ngantor.usecase;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class LuxSensor {
    private float currentLux;

    public LuxSensor(Context context) {
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        Sensor lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        if (lightSensor != null) {
            sensorManager.registerListener(new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    currentLux = event.values[0];
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                }
            }, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            currentLux = -1;
        }
    }

    public float getLuxLevel() {
        String TAG = "Lux Level";

        Log.i(TAG, "Lux Level: " + currentLux + " lux");
        return currentLux;
    }
}
