package org.ytu.adem.datacollector.sensors.accelerometer;

import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.os.Bundle;

import org.ytu.adem.datacollector.R;
import org.ytu.adem.datacollector.sensors.base.BaseSensorActivity;

public class AccelerometerActivity extends BaseSensorActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer);
        String preferencesFileName = getString(R.string.accelerometer_config_fileName);
        super.init(Sensor.TYPE_ACCELEROMETER, preferencesFileName);
        initSensorName(preferencesFileName);
    }

    private void initSensorName(String preferencesFileName) {
        SharedPreferences.Editor editor = getSharedPreferences(preferencesFileName, MODE_PRIVATE).edit();
        editor.putString(getString(R.string.shared_preferences_sensorName), getString(R.string.sensor_accelerometer));
        editor.commit();
    }


}