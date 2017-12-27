package org.ytu.adem.datacollector.sensors.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.widget.Toast;

import org.ytu.adem.datacollector.R;
import org.ytu.adem.datacollector.enums.Action;
import org.ytu.adem.datacollector.sensors.accelerometer.AccelerometerRecorder;
import org.ytu.adem.datacollector.sensors.gravity.GravityRecorder;
import org.ytu.adem.datacollector.sensors.gyroscope.GyroscopeRecorder;

/**
 * Created by Adem on 16.10.2017.
 */

public class Receiver extends BroadcastReceiver {
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        String action = intent.getStringExtra("action");
        int sensorType = intent.getIntExtra("sensorType", 0);
        Boolean actionStart = initActionStart(action);
        if (actionStart == null) {
            Toast.makeText(context, String.format("Action not defined", sensorType, action), Toast.LENGTH_LONG).show();
            return;
        }
        String sensorName = startAction(sensorType, actionStart);
        if (actionStart)
            Toast.makeText(context, context.getResources().getString(R.string.record_started, sensorName), Toast.LENGTH_LONG).show();
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

    private String startAction(int sensorType, boolean actionStart) {
        String sensorName;
        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                Intent accelerometerIntent = new Intent(context, AccelerometerRecorder.class);
                sensorName = context.getResources().getString(R.string.sensor_accelerometer);
                if (actionStart) {
                    context.startService(accelerometerIntent);
                } else {
                    context.stopService(accelerometerIntent);
                }
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                sensorName = context.getResources().getString(R.string.sensor_linear_acceleration);
                break;
            case Sensor.TYPE_GRAVITY:
                Intent gravityIntent = new Intent(context, GravityRecorder.class);
                sensorName = context.getResources().getString(R.string.sensor_gravity);
                if (actionStart) {
                    context.startService(gravityIntent);
                } else {
                    context.stopService(gravityIntent);
                }
                break;
            case Sensor.TYPE_GYROSCOPE:
                Intent gyroscopeIntent = new Intent(context, GyroscopeRecorder.class);
                sensorName = context.getResources().getString(R.string.sensor_gyroscope);
                if (actionStart) {
                    context.startService(gyroscopeIntent);
                } else {
                    context.stopService(gyroscopeIntent);
                }
                break;
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                sensorName = context.getResources().getString(R.string.sensor_relative_humidity);
                break;
            case Sensor.TYPE_LIGHT:
                sensorName = context.getResources().getString(R.string.sensor_light);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                sensorName = context.getResources().getString(R.string.sensor_magnetic_field);
                break;
            case Sensor.TYPE_PRESSURE:
                sensorName = context.getResources().getString(R.string.sensor_pressure);
                break;
            case Sensor.TYPE_PROXIMITY:
                sensorName = context.getResources().getString(R.string.sensor_proximity);
                break;
            case Sensor.TYPE_ROTATION_VECTOR:
                sensorName = context.getResources().getString(R.string.sensor_rotation_vector);
                break;
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                sensorName = context.getResources().getString(R.string.sensor_ambient_temperature);
                break;
            default:
                sensorName = "Bilinmeyen";
        }
        return sensorName;
    }
}
