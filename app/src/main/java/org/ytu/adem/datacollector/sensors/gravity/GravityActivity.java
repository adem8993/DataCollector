package org.ytu.adem.datacollector.sensors.gravity;

import android.hardware.Sensor;
import android.os.Bundle;

import org.ytu.adem.datacollector.R;
import org.ytu.adem.datacollector.sensors.base.BaseSensorActivity;

/**
 * Created by Adem on 16.05.2017.
 */
public class GravityActivity extends BaseSensorActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gravity);
        super.init(Sensor.TYPE_GRAVITY, getString(R.string.gravity_config_fileName));
    }

}