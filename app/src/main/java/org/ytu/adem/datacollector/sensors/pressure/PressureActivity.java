package org.ytu.adem.datacollector.sensors.pressure;

import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.os.Bundle;

import org.ytu.adem.datacollector.R;
import org.ytu.adem.datacollector.sensors.base.BaseSensorActivity;

/**
 * Created by Adem on 16.05.2017.
 */
public class PressureActivity extends BaseSensorActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pressure);
        String preferencesFileName = getString(R.string.pressure_config_fileName);
        super.init(Sensor.TYPE_PRESSURE, preferencesFileName);
        initSensorName(preferencesFileName);
    }

    private void initSensorName(String preferencesFileName) {
        SharedPreferences.Editor editor = getSharedPreferences(preferencesFileName, MODE_PRIVATE).edit();
        editor.putString(getString(R.string.shared_preferences_sensorName), getString(R.string.sensor_pressure));
        editor.commit();
    }
}
