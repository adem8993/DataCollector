package org.ytu.adem.datacollector.sensors.accelerometer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import org.ytu.adem.datacollector.sensors.service.recorder.AccelerometerRecorder;

/**
 * Created by Adem on 16.10.2017.
 */

public class Receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        intent.getStringExtra("action");
        intent.getStringExtra("sensor");
        context.startService(new Intent(context, AccelerometerRecorder.class));
        Toast.makeText(context, "Alarm Triggered and SMS Sent", Toast.LENGTH_LONG).show();
    }
}
