package org.ytu.adem.datacollector.sensors.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.widget.Toast;

import org.ytu.adem.datacollector.R;
import org.ytu.adem.datacollector.enums.Action;

/**
 * Created by Adem on 16.10.2017.
 */

public class Receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getStringExtra("action");
        int sensorType = intent.getIntExtra("sensorType", 0);
        String sensorName = intent.getStringExtra("sensorName");
        String configFileName = intent.getStringExtra("configFileName");
        Resources resources = context.getResources();
        Boolean actionStart = initActionStart(action);
        if (actionStart == null) {
            Toast.makeText(context, String.format("Action not defined", sensorName, action), Toast.LENGTH_LONG).show();
            return;
        }
        Intent recorderIntent = new Intent(context, Recorder.class);
        recorderIntent.putExtra("sensorType", sensorType);
        recorderIntent.putExtra("configFileName", configFileName);
        if (actionStart) {
            context.startService(recorderIntent);
        } else {
            context.stopService(recorderIntent);
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
