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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by adem on 28.12.2017.
 */

public class MultipleRecorder extends BaseRecorderService {
    private String configFileName;
    private long[] lastUpdate = new long[10];
    private int[] frequency = new int[10];
    private String[] tempValuesToWrite = new String[10];
    private LinkedList<Integer> sensorList = new LinkedList<Integer>();
    private Map<Integer, String> selectedSensors = new HashMap<>();
    Iterator iterator;
    private List recordedSensors = new ArrayList();

    public MultipleRecorder() {
        super("MultipleRecorder");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sensorManager = (SensorManager) getApplicationContext()
                .getSystemService(SENSOR_SERVICE);
        selectedSensors = (Map<Integer, String>) intent.getSerializableExtra("selectedSensors");
        Iterator selectedIterator = selectedSensors.entrySet().iterator();
        while(selectedIterator.hasNext()) {
            Map.Entry selectedSensor = (Map.Entry) selectedIterator.next();
            Sensor sensor = sensorManager.getDefaultSensor((int) selectedSensor.getKey());
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
            sensorList.add((int) selectedSensor.getKey());
        }
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        Sensor gravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        sensorList.add(Sensor.TYPE_ACCELEROMETER);
        sensorList.add(Sensor.TYPE_GYROSCOPE);
        sensorList.add(Sensor.TYPE_GRAVITY);
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
        frequency[Sensor.TYPE_ACCELEROMETER] = 7;
        frequency[Sensor.TYPE_GRAVITY] = 5;
        frequency[Sensor.TYPE_GYROSCOPE] = 3;
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
        if (actualTime - lastUpdate[sensorType] < ONE_SECOND_NANO / frequency[sensorType]) return;
        lastUpdate[sensorType] = actualTime;
        if (!recordedSensors.contains(sensorType)) {
            tempValuesToWrite[sensorType] = new ThreeAxisValue(
                    Util.formatFloatValueByPrecision(event.values[0], precision),
                    Util.formatFloatValueByPrecision(event.values[1], precision),
                    Util.formatFloatValueByPrecision(event.values[2], precision)).
                    toString();
            recordedSensors.add(sensorType);
        } else {
            addToValuesToWrite(event, sensorType);
            recordedSensors.add(sensorType);
        }
        if (recordedSensors.size() == sensorList.size()) {
            valuesToWrite.add(getValuesWithDate());
            clearTempValuesToWrite();
            recordedSensors.clear();
        }
    }

    private String getEmptyRecord() {
        String emptyValue = "x.";
        for (int i = 0; i < precision; i++) {
            emptyValue += "x";
        }
        return new ThreeAxisValue(emptyValue, emptyValue, emptyValue).toString();
    }

    private void addEmptyRecord(int sensorType) {
        tempValuesToWrite[sensorType] = getEmptyRecord();
    }

    private void addToValuesToWrite(SensorEvent event, int sensorType) {
        valuesToWrite.add(getValuesWithDate());
        clearTempValuesToWrite();
        tempValuesToWrite[sensorType] = new ThreeAxisValue(
                Util.formatFloatValueByPrecision(event.values[0], precision),
                Util.formatFloatValueByPrecision(event.values[1], precision),
                Util.formatFloatValueByPrecision(event.values[2], precision)).
                toString();
        recordedSensors.clear();
    }

    private String getValuesWithDate() {
        String valuesWithDate = fileDateFormat.format(new Date());
        fillEmptyRecords();
        iterator = sensorList.iterator();
        while (iterator.hasNext()) {
            int sensorType = (int) iterator.next();
            valuesWithDate += " || " + tempValuesToWrite[sensorType];
        }
        return valuesWithDate;
    }

    private void fillEmptyRecords() {
        iterator = sensorList.iterator();
        while (iterator.hasNext()) {
            int sensorType = (int) iterator.next();
            if (tempValuesToWrite[sensorType] == null) addEmptyRecord(sensorType);
        }
    }

    private void clearTempValuesToWrite() {
        for (int i = 0; i < 10; i++) {
            tempValuesToWrite[i] = null;
        }
    }
}
