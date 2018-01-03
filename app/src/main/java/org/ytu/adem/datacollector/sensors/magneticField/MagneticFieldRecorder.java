package org.ytu.adem.datacollector.sensors.magneticField;

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
 * Created by adem on 3.01.2018.
 */

public class MagneticFieldRecorder extends BaseRecorderService {
    private String configFileName;
    private String fileHeaderText;

    public MagneticFieldRecorder() {
        super("MagneticField Recorder");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sensorManager = (SensorManager) getApplicationContext()
                .getSystemService(SENSOR_SERVICE);
        Sensor magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        fileHeaderText = Util.prepareFileHeader(getString(R.string.sensor_magnetic_field), frequency, precision);
        sensorManager.registerListener(this, magneticField, SensorManager.SENSOR_DELAY_NORMAL);
        lastUpdate = System.currentTimeMillis();
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        configFileName = getString(R.string.magnetic_field_config_fileName);
        preferences = getSharedPreferences(configFileName, MODE_PRIVATE);
        frequency = preferences.getInt("frequency", 1);
        precision = preferences.getInt("precision", 2);
        fileDateFormat = new SimpleDateFormat(preferences.getString("dateFormat", "MM-dd"));
    }

    @Override
    public void onDestroy() {
        sensorManager.unregisterListener(this);
        String fileName = preferences.getString(getString(R.string.shared_preferences_fileName), "a");
        writeSensorDataToFile(configFileName, fileName, fileHeaderText);
        removeScheduleActiveFlag();
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
