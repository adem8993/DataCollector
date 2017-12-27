package org.ytu.adem.datacollector.sensors.common;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import org.ytu.adem.datacollector.R;
import org.ytu.adem.datacollector.model.ThreeAxisValue;
import org.ytu.adem.datacollector.sensors.base.BaseRecorderService;
import org.ytu.adem.datacollector.util.Util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Adem on 21.11.2017.
 */

public class Recorder extends BaseRecorderService {
    private int sensorType;
    private String configFileName;

    public Recorder() {
        super("Recorder");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sensorManager = (SensorManager) getApplicationContext()
                .getSystemService(SENSOR_SERVICE);
        this.sensorType = intent.getIntExtra("sensorType", 0);
        this.configFileName = intent.getStringExtra("configFileName");
        Sensor sensor = sensorManager.getDefaultSensor(sensorType);
        preferences = getSharedPreferences(this.configFileName, MODE_PRIVATE);
        frequency = preferences.getInt("frequency", 1);
        precision = preferences.getInt("precision", 2);
        fileDateFormat = new SimpleDateFormat(preferences.getString("dateFormat", "MM-dd"));
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        lastUpdate = System.currentTimeMillis();
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void onDestroy() {
        sensorManager.unregisterListener(this);
        String fileName = preferences.getString(getString(R.string.shared_preferences_fileName), "a");
        writeSensorDataToFile(this.configFileName, fileName);
        checkAlarmActivation();
        super.onDestroy();
    }

    private void checkAlarmActivation() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(getString(R.string.shared_preferences_scheduleActive));
        editor.commit();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long actualTime = event.timestamp;
        if (actualTime - lastUpdate < ONE_SECOND_NANO / frequency) return;
        lastUpdate = actualTime;
        valuesToWrite.add(new ThreeAxisValue(fileDateFormat.format(new Date()),
                Util.formatFloatValueByPrecision(event.values[0], precision),
                Util.formatFloatValueByPrecision(event.values[1], precision),
                Util.formatFloatValueByPrecision(event.values[2], precision)).
                toString());
    }
}
