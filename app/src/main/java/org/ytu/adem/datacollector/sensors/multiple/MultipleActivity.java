package org.ytu.adem.datacollector.sensors.multiple;

import android.hardware.Sensor;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import org.ytu.adem.datacollector.R;
import org.ytu.adem.datacollector.adapter.ViewPagerAdapter;
import org.ytu.adem.datacollector.sensors.common.ConfigFragment;
import org.ytu.adem.datacollector.sensors.common.RecordFragment;
import org.ytu.adem.datacollector.sensors.common.ScheduleFragment;

/**
 * Created by Adem on 31.12.2017.
 */

public class MultipleActivity extends FragmentActivity {
    private int[] tabIcons = {android.R.drawable.ic_menu_my_calendar,
            android.R.drawable.ic_menu_preferences, android.R.drawable.ic_menu_agenda};
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple);
        String preferencesFileName = getString(R.string.multiple_config_fileName);
        init(Sensor.TYPE_ALL, preferencesFileName);
    }

    protected void init(int sensorType, String configFileName) {
        setupViewPager(sensorType, configFileName);
        initTabLayout();
    }

    private void setupViewPager(int sensorType, String configFileName) {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ScheduleFragment(sensorType, configFileName, true), getString(R.string.schedule));
        adapter.addFragment(new ConfigFragment(configFileName, true), getString(R.string.config));
        adapter.addFragment(new RecordFragment(configFileName), getString(R.string.records));
        viewPager.setAdapter(adapter);
    }

    private void initTabLayout() {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }
}
