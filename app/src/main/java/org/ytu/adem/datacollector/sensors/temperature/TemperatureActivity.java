package org.ytu.adem.datacollector.sensors.temperature;

import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.ytu.adem.datacollector.R;
import org.ytu.adem.datacollector.sensors.base.BaseSensorActivity;

/**
 * Created by Adem on 16.05.2017.
 */
public class TemperatureActivity extends BaseSensorActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);
        String preferencesFileName = getString(R.string.temperature_config_fileName);
        super.init(Sensor.TYPE_AMBIENT_TEMPERATURE, preferencesFileName);
        initSensorName(preferencesFileName);
    }

    private void initSensorName(String preferencesFileName) {
        SharedPreferences.Editor editor = getSharedPreferences(preferencesFileName, MODE_PRIVATE).edit();
        editor.putString(getString(R.string.shared_preferences_sensorName), getString(R.string.sensor_ambient_temperature));
        editor.commit();
    }
}
