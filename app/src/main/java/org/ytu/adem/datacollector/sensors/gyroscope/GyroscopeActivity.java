package org.ytu.adem.datacollector.sensors.gyroscope;

/**
 * Created by adem on 26.12.2017.
 */

import android.hardware.Sensor;
import android.os.Bundle;

import org.ytu.adem.datacollector.R;
import org.ytu.adem.datacollector.sensors.base.BaseSensorActivity;

public class GyroscopeActivity extends BaseSensorActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gyroscope);
        super.init(Sensor.TYPE_GYROSCOPE, getString(R.string.gyroscope_config_fileName));
    }

}