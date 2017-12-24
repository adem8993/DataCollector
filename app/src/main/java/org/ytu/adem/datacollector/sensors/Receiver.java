package org.ytu.adem.datacollector.sensors;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.widget.Toast;

import org.ytu.adem.datacollector.R;
import org.ytu.adem.datacollector.enums.Action;
import org.ytu.adem.datacollector.enums.Sensor;
import org.ytu.adem.datacollector.sensors.accelerometer.AccelerometerRecorder;

/**
 * Created by Adem on 16.10.2017.
 */

public class Receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getStringExtra("action");
        String sensor = intent.getStringExtra("sensor");
        String sensorName = "Bilinmeyen";
        Resources resources = context.getResources();
        Boolean actionStart = initActionStart(action);
        if (actionStart == null) {
            Toast.makeText(context, String.format("Action not defined", sensor, action), Toast.LENGTH_LONG).show();
            return;
        }
        if (Sensor.ACCELEROMETER.toString().equals(sensor)) {
            Intent accelerometerIntent = new Intent(context, AccelerometerRecorder.class);
            sensorName = resources.getString(R.string.sensor_accelerometer);
            if (actionStart) {
                context.startService(accelerometerIntent);
            } else {
                context.stopService(accelerometerIntent);
            }
        }
        if(actionStart) Toast.makeText(context, resources.getString(R.string.record_started, sensorName), Toast.LENGTH_LONG).show();
    }

    private Boolean initActionStart(String action) {
        Boolean actionStart = null;
        if (Action.START.toString().equals(action)) {
            actionStart = true;
        } else if (Action.STOP.toString().equals(action)) {
            actionStart = false;
        }
        return actionStart;
    }

}
