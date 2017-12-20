package org.ytu.adem.datacollector.sensors.accelerometer;

import android.hardware.Sensor;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.widget.ImageButton;

import org.ytu.adem.datacollector.R;
import org.ytu.adem.datacollector.sensors.base.BaseSensorActivity;
import org.ytu.adem.datacollector.util.Util;

public class AccelerometerActivity extends BaseSensorActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer);
        super.init(Sensor.TYPE_ACCELEROMETER);
    }

}