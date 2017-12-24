package org.ytu.adem.datacollector.sensors.base;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.widget.Toast;

import org.ytu.adem.datacollector.R;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Adem on 24.12.2017.
 */

public class BaseRecorderService extends IntentService implements SensorEventListener {
    protected static final int ONE_SECOND_NANO = 1000000000;
    protected List<String> valuesToWrite = new ArrayList<>();
    protected int frequency;
    protected int precision;
    protected SensorManager sensorManager;
    protected long lastUpdate;
    protected SharedPreferences preferences;
    protected SimpleDateFormat fileDateFormat;

    public BaseRecorderService(String text) {
        super(text);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent ıntent) {

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    protected void writeSensorDataToFile(String fileName) {
        //TODO: External veya internal storage'a yazma desteği olmalı.
        File path = getExternalFilesDir(null);
        fileName += "(" + fileDateFormat.format(new Date()) + ").csv";
        try {
            FileWriter writer = new FileWriter(new File(path, fileName));
            for (String line : valuesToWrite) {
                writer.write(line + "\n");
            }
            writer.close();
            showToast("Sensör verileri dosyaya yazıldı.\nDosya adı: " + fileName);
        } catch (IOException e) {
            showToast("Veriler dosyaya yazılamadı. Dosya Adı:");
            System.out.println(e);
        }
    }

    protected void showToast(String message) {
        final String msg = message;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }
}
