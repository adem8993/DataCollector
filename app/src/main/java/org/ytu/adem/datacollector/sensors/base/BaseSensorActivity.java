package org.ytu.adem.datacollector.sensors.base;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.ytu.adem.datacollector.R;
import org.ytu.adem.datacollector.adapter.ViewPagerAdapter;
import org.ytu.adem.datacollector.sensors.InfoDialog;
import org.ytu.adem.datacollector.sensors.common.ConfigFragment;
import org.ytu.adem.datacollector.sensors.common.RecordFragment;
import org.ytu.adem.datacollector.sensors.common.ScheduleFragment;
import org.ytu.adem.datacollector.sensors.common.ThreeAxisMonitorFragment;

/**
 * Created by Adem on 10.12.2017.
 */

public class BaseSensorActivity extends FragmentActivity {
    private int[] tabIcons = {android.R.drawable.ic_menu_search, android.R.drawable.ic_menu_my_calendar,
            android.R.drawable.ic_menu_preferences, android.R.drawable.ic_menu_agenda};
    private ViewPager viewPager;

    protected void init(int sensorType, String configFileName) {
        setupViewPager(sensorType, configFileName);
        initTabLayout();
        initSensorInfo(sensorType);
        initSubTitle(sensorType);
    }

    private void initSubTitle(int sensorType) {
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(sensorType);
        TextView subTitle = (TextView) findViewById(R.id.toolbar_subTitle);
        subTitle.setText(getString(R.string.power_consumption, sensor.getPower()));
    }

    private void setupViewPager(int sensorType, String configFileName) {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ThreeAxisMonitorFragment(sensorType, configFileName), getString(R.string.monitor));
        adapter.addFragment(new ScheduleFragment(sensorType, configFileName ,false), getString(R.string.schedule));
        adapter.addFragment(new ConfigFragment(configFileName, false), getString(R.string.config));
        adapter.addFragment(new RecordFragment(configFileName), getString(R.string.records));
        viewPager.setAdapter(adapter);
    }

    private void initTabLayout() {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
    }

    private void initSensorInfo(final int sensorType) {
        final FragmentManager fm = getSupportFragmentManager();
        ImageButton imageButton = (ImageButton) findViewById(R.id.info_button);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InfoDialog info = new InfoDialog();
                Bundle args = new Bundle();
                args.putInt("sensorType", sensorType);
                info.setArguments(args);
                info.show(fm, "InfoDialog");
            }
        });
    }

}
