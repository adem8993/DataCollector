package org.ytu.adem.datacollector.sensors.service.recorder;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.widget.Toast;

import org.ytu.adem.datacollector.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adem on 21.11.2017.
 */

public class AccelerometerRecorder extends IntentService implements SensorEventListener {
    private static final int ONE_SECOND_NANO = 1000000000;
    List<String> valuesToWrite = new ArrayList<>();

    private SensorManager sensorManager;
    private long lastUpdate;
    private SharedPreferences preferences;

    public AccelerometerRecorder() {
        super("a");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String dataString = intent.getDataString();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        sensorManager = (SensorManager) getApplicationContext()
                .getSystemService(SENSOR_SERVICE);
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        lastUpdate = System.currentTimeMillis();
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        Toast.makeText(getApplicationContext(), "Started", Toast.LENGTH_SHORT).show();
        super.onCreate();
        preferences = getSharedPreferences(getResources().getString(R.string.accelerometer_config_fileName), MODE_PRIVATE);

    }

    private void getAccelerometer(SensorEvent event) {
        long actualTime = event.timestamp;
        if (actualTime - lastUpdate < ONE_SECOND_NANO / preferences.getInt("frequency", 1)) return;
        float[] values = event.values;
        // Movement
        float x = values[0];
        float y = values[1];
        float z = values[2];

        float accelationSquareRoot = (x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        actualTime = System.currentTimeMillis();
        if (accelationSquareRoot >= 7) //
        {
            if (actualTime - lastUpdate < 2000) {
                return;
            }
            lastUpdate = actualTime;
            Toast.makeText(this,
                    "Device was shuffed _ " + accelationSquareRoot,
                    Toast.LENGTH_SHORT).show();
            Vibrator v = (Vibrator) getApplicationContext().getSystemService(VIBRATOR_SERVICE);
            v.vibrate(1000);
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        }
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        sensorManager.unregisterListener(this);
        Toast.makeText(this, "Destroy", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }


}
