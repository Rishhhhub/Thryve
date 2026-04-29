package com.example.thryve;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class StepSensorHelper implements SensorEventListener {
    private static StepSensorHelper instance;
    private SensorManager sensorManager;
    private Sensor stepSensor;
    private SharedPreferences prefs;
    
    private StepUpdateListener listener;
    
    public interface StepUpdateListener {
        void onStepsUpdated(int steps);
    }
    
    private StepSensorHelper(Context context) {
        prefs = context.getSharedPreferences("thryve_prefs", Context.MODE_PRIVATE);
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        }
    }
    
    public static StepSensorHelper getInstance(Context context) {
        if (instance == null) {
            instance = new StepSensorHelper(context.getApplicationContext());
        }
        return instance;
    }
    
    public boolean isSensorPresent() {
        return stepSensor != null;
    }
    
    public void startListening(StepUpdateListener listener) {
        this.listener = listener;
        if (stepSensor != null) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI);
        }
        // Immediately return last known step count so UI isn't blank
        int lastCount = prefs.getInt("last_step_count", 0);
        if (listener != null) {
            listener.onStepsUpdated(lastCount);
        }
    }
    
    public void stopListening() {
        this.listener = null;
        if (stepSensor != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            int totalSteps = (int) event.values[0];
            int stepBaseline = prefs.getInt("step_baseline", -1);
            
            if (stepBaseline == -1 || totalSteps < stepBaseline) {
                // First reading or device rebooted
                stepBaseline = totalSteps;
                prefs.edit().putInt("step_baseline", stepBaseline).apply();
            }
            
            int currentSteps = totalSteps - stepBaseline;
            prefs.edit().putInt("last_step_count", currentSteps).apply();
            
            if (listener != null) {
                listener.onStepsUpdated(currentSteps);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not needed
    }
}
