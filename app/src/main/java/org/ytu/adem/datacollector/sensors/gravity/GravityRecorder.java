package org.ytu.adem.datacollector.sensors.gravity;

import android.content.Intent;
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
 * Created by Adem on 27.12.2017.
 */

public class GravityRecorder extends BaseRecorderService {
    private String configFileName;

    public GravityRecorder() {
        super("GravityRecorder");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sensorManager = (SensorManager) getApplicationContext()
                .getSystemService(SENSOR_SERVICE);
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        lastUpdate = System.currentTimeMillis();
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        configFileName = getString(R.string.gravity_config_fileName);
        preferences = getSharedPreferences(configFileName, MODE_PRIVATE);
        frequency = preferences.getInt("frequency", 1);
        precision = preferences.getInt("precision", 2);
        fileDateFormat = new SimpleDateFormat(preferences.getString("dateFormat", "MM-dd"));
    }

    @Override
    public void onDestroy() {
        sensorManager.unregisterListener(this);
        String fileName = preferences.getString(getString(R.string.shared_preferences_fileName), "Tanımsız");
        writeSensorDataToFile(configFileName, fileName, "");
        super.onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long actualTime = event.timestamp;
        if (actualTime - lastUpdate < ONE_SECOND_NANO / frequency) return;
        lastUpdate = actualTime;
        String valuesWithDate = fileDateFormat.format(new Date()) + " || " +
                new ThreeAxisValue(
                        Util.formatFloatValueByPrecision(event.values[0], precision),
                        Util.formatFloatValueByPrecision(event.values[1], precision),
                        Util.formatFloatValueByPrecision(event.values[2], precision)).toString();
        valuesToWrite.add(valuesWithDate);
    }
}
