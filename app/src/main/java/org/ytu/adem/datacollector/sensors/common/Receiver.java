package org.ytu.adem.datacollector.sensors.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.widget.Toast;

import org.ytu.adem.datacollector.R;
import org.ytu.adem.datacollector.enums.Action;
import org.ytu.adem.datacollector.sensors.acceleration.AccelerationRecorder;
import org.ytu.adem.datacollector.sensors.accelerometer.AccelerometerRecorder;
import org.ytu.adem.datacollector.sensors.gravity.GravityRecorder;
import org.ytu.adem.datacollector.sensors.gyroscope.GyroscopeRecorder;
import org.ytu.adem.datacollector.sensors.humidity.HumidityRecorder;
import org.ytu.adem.datacollector.sensors.light.LightRecorder;
import org.ytu.adem.datacollector.sensors.magneticField.MagneticFieldRecorder;
import org.ytu.adem.datacollector.sensors.multiple.MultipleRecorder;
import org.ytu.adem.datacollector.sensors.pressure.PressureRecorder;
import org.ytu.adem.datacollector.sensors.proximity.ProximityRecorder;
import org.ytu.adem.datacollector.sensors.rotationVector.RotationVectorRecorder;
import org.ytu.adem.datacollector.sensors.temperature.TemperatureRecorder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Adem on 16.10.2017.
 */

public class Receiver extends BroadcastReceiver {
    private Context context;
    private Map<Integer, String> selectedSensors = new HashMap<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        String action = intent.getStringExtra("action");
        int sensorType = intent.getIntExtra("sensorType", 0);
        selectedSensors = (Map<Integer, String>) intent.getSerializableExtra("selectedSensors");
        Boolean actionStart = initActionStart(action);
        if (actionStart == null) {
            Toast.makeText(context, String.format("Action not defined", sensorType, action), Toast.LENGTH_LONG).show();
            return;
        }
        String sensorName = startAction(sensorType, actionStart);
        if (actionStart)
            Toast.makeText(context, context.getResources().getString(R.string.record_started, sensorName), Toast.LENGTH_LONG).show();
        else if (selectedSensors != null) {
            Toast.makeText(context, sensorName + " sensör için kayıt işlemi başladı.", Toast.LENGTH_LONG).show();

        }
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
        Intent intent = null;
        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                intent = new Intent(context, AccelerometerRecorder.class);
                sensorName = context.getResources().getString(R.string.sensor_accelerometer);
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                intent = new Intent(context, AccelerationRecorder.class);
                sensorName = context.getResources().getString(R.string.sensor_linear_acceleration);
                break;
            case Sensor.TYPE_GRAVITY:
                intent = new Intent(context, GravityRecorder.class);
                sensorName = context.getResources().getString(R.string.sensor_gravity);
                break;
            case Sensor.TYPE_GYROSCOPE:
                intent = new Intent(context, GyroscopeRecorder.class);
                sensorName = context.getResources().getString(R.string.sensor_gyroscope);
                break;
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                intent = new Intent(context, HumidityRecorder.class);
                sensorName = context.getResources().getString(R.string.sensor_relative_humidity);
                break;
            case Sensor.TYPE_LIGHT:
                intent = new Intent(context, LightRecorder.class);
                sensorName = context.getResources().getString(R.string.sensor_light);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                intent = new Intent(context, MagneticFieldRecorder.class);
                sensorName = context.getResources().getString(R.string.sensor_magnetic_field);
                break;
            case Sensor.TYPE_PRESSURE:
                intent = new Intent(context, PressureRecorder.class);
                sensorName = context.getResources().getString(R.string.sensor_pressure);
                break;
            case Sensor.TYPE_PROXIMITY:
                intent = new Intent(context, ProximityRecorder.class);
                sensorName = context.getResources().getString(R.string.sensor_proximity);
                break;
            case Sensor.TYPE_ROTATION_VECTOR:
                intent = new Intent(context, RotationVectorRecorder.class);
                sensorName = context.getResources().getString(R.string.sensor_rotation_vector);
                break;
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                intent = new Intent(context, TemperatureRecorder.class);
                sensorName = context.getResources().getString(R.string.sensor_ambient_temperature);
                break;
            case Sensor.TYPE_ALL:
                intent = new Intent(context, MultipleRecorder.class);
                intent.putExtra("selectedSensors", (Serializable) selectedSensors);
                sensorName = context.getResources().getString(R.string.sensor_all);
                break;
            default:
                sensorName = "Bilinmeyen";
                break;
        }
        if (actionStart) {
            context.startService(intent);
        } else {
            context.stopService(intent);
        }
        return sensorName;
    }
}
