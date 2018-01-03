package org.ytu.adem.datacollector.sensors.acceleration;

import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.os.Bundle;

import org.ytu.adem.datacollector.R;
import org.ytu.adem.datacollector.sensors.base.BaseSensorActivity;

public class AccelerationActivity extends BaseSensorActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acceleration);
        String preferencesFileName = getString(R.string.acceleration_config_fileName);
        super.init(Sensor.TYPE_LINEAR_ACCELERATION, preferencesFileName);
        initSensorName(preferencesFileName);
    }

    private void initSensorName(String preferencesFileName) {
        SharedPreferences.Editor editor = getSharedPreferences(preferencesFileName, MODE_PRIVATE).edit();
        editor.putString(getString(R.string.shared_preferences_sensorName), getString(R.string.sensor_linear_acceleration));
        editor.commit();
    }


}