package org.ytu.adem.datacollector.sensors.base;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;

import org.ytu.adem.datacollector.R;
import org.ytu.adem.datacollector.adapter.ViewPagerAdapter;
import org.ytu.adem.datacollector.sensors.InfoDialog;
import org.ytu.adem.datacollector.sensors.common.ConfigFragment;
import org.ytu.adem.datacollector.sensors.common.ThreeAxisMonitorFragment;
import org.ytu.adem.datacollector.sensors.common.RecordFragment;
import org.ytu.adem.datacollector.sensors.common.ScheduleFragment;

/**
 * Created by Adem on 10.12.2017.
 */

public class BaseSensorActivity extends FragmentActivity {
    protected int[] tabIcons = {android.R.drawable.ic_menu_search, android.R.drawable.ic_menu_my_calendar,
            android.R.drawable.ic_menu_preferences, android.R.drawable.ic_menu_agenda};
    protected FragmentManager fm = getSupportFragmentManager();
    private ViewPager viewPager;
    private TabLayout tabLayout;

    protected void init(int sensorType, String configFileName) {
        setupViewPager(sensorType, configFileName);
        initTabLayout();
        initSensorInfo(sensorType);
    }

    private void setupViewPager(int sensorType, String configFileName) {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ThreeAxisMonitorFragment(sensorType, configFileName), getString(R.string.monitor));
        adapter.addFragment(new ScheduleFragment(sensorType, configFileName), getString(R.string.schedule));
        adapter.addFragment(new ConfigFragment(configFileName), getString(R.string.config));
        adapter.addFragment(new RecordFragment(configFileName), getString(R.string.records));
        viewPager.setAdapter(adapter);

    }

    private void initTabLayout() {
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
    }

    private void initSensorInfo(final int sensorType) {
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
