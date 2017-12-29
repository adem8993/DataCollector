package org.ytu.adem.datacollector.sensors;

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
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by adem on 28.12.2017.
 */

public class MultipleRecorder extends BaseRecorderService {
    private String configFileName;
    private long[] lastUpdate = new long[10];
    private String tempValuesToWrite = new String();
    private LinkedList<Integer> sensorList = new LinkedList<Integer>();
    Iterator iterator;
    private int expectedSensor;

    public MultipleRecorder() {
        super("MultipleRecorder");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sensorManager = (SensorManager) getApplicationContext()
                .getSystemService(SENSOR_SERVICE);
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        Sensor gravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        sensorList.add(Sensor.TYPE_ACCELEROMETER);
        sensorList.add(Sensor.TYPE_GYROSCOPE);
        sensorList.add(Sensor.TYPE_GRAVITY);
        iterator = sensorList.iterator();
        expectedSensor = (Integer) iterator.next();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gravity, SensorManager.SENSOR_DELAY_NORMAL);
        lastUpdate[Sensor.TYPE_ACCELEROMETER] = System.currentTimeMillis();
        lastUpdate[Sensor.TYPE_GYROSCOPE] = System.currentTimeMillis();
        return START_STICKY;
    }

    @Override
    public void onHandleIntent(Intent intent) {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        configFileName = getString(R.string.accelerometer_config_fileName);
        preferences = getSharedPreferences(configFileName, MODE_PRIVATE);
        frequency = preferences.getInt("frequency", 1);
        precision = preferences.getInt("precision", 2);
        fileDateFormat = new SimpleDateFormat(preferences.getString("dateFormat", "MM-dd"));
    }

    @Override
    public void onDestroy() {
        sensorManager.unregisterListener(this);
        String fileName = preferences.getString(getString(R.string.shared_preferences_fileName), "a");
        writeSensorDataToFile(configFileName, fileName);
        super.onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long actualTime = event.timestamp;
        int sensorType = event.sensor.getType();
        if (expectedSensor != sensorType) return;
        if (actualTime - lastUpdate[sensorType] < ONE_SECOND_NANO / frequency) return;
        lastUpdate[sensorType] = actualTime;
        if (iterator.hasNext()) {
            tempValuesToWrite += new ThreeAxisValue(
                    Util.formatFloatValueByPrecision(event.values[0], precision),
                    Util.formatFloatValueByPrecision(event.values[1], precision),
                    Util.formatFloatValueByPrecision(event.values[2], precision)).
                    toString() + " || ";
        } else {
            tempValuesToWrite += new ThreeAxisValue(
                    Util.formatFloatValueByPrecision(event.values[0], precision),
                    Util.formatFloatValueByPrecision(event.values[1], precision),
                    Util.formatFloatValueByPrecision(event.values[2], precision)).
                    toString();

            String valuesWithDate = fileDateFormat.format(new Date()) + " || " + tempValuesToWrite;
            valuesToWrite.add(valuesWithDate);
            tempValuesToWrite = "";
            iterator = sensorList.iterator();
        }
        expectedSensor = (Integer) iterator.next();

    }
}
