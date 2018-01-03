package org.ytu.adem.datacollector.sensors.magneticField;

import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.os.Bundle;

import org.ytu.adem.datacollector.R;
import org.ytu.adem.datacollector.sensors.base.BaseSensorActivity;

/**
 * Created by Adem on 16.05.2017.
 */
public class MagneticFieldActivity extends BaseSensorActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magnetic_field);
        String preferencesFileName = getString(R.string.magnetic_field_config_fileName);
        super.init(Sensor.TYPE_MAGNETIC_FIELD, preferencesFileName);
        initSensorName(preferencesFileName);
    }

    private void initSensorName(String preferencesFileName) {
        SharedPreferences.Editor editor = getSharedPreferences(preferencesFileName, MODE_PRIVATE).edit();
        editor.putString(getString(R.string.shared_preferences_sensorName), getString(R.string.sensor_magnetic_field));
        editor.commit();
    }
}
