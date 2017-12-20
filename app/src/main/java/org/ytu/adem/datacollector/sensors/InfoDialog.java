package org.ytu.adem.datacollector.sensors;

import android.app.Dialog;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;
import org.ytu.adem.datacollector.R;

/**
 * Created by Adem on 1.12.2017.
 */

public class InfoDialog extends DialogFragment {
    private SensorManager sensorManager;
    private TextView name;
    private TextView vendor;
    private TextView version;
    private TextView maxRange;
    private TextView resolution;
    private TextView power;
    private TextView minDelay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.sensor_info_dialog, container, false);
        // Do something else
        return rootView;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View layout = getActivity().getLayoutInflater().inflate(R.layout.sensor_info_dialog, null, false);
        assert layout != null;
        //build the alert dialog child of this fragment
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        //restore the background_color and layout_gravity that Android strips
        b.getContext().getTheme().applyStyle(R.style.AppTheme_NoActionBar, true);
        b.setView(layout);
        return b.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        initTextViews();
        initTextValues();
    }

    private void initTextViews() {
        name = (TextView) getDialog().findViewById(R.id.name);
        vendor = (TextView) getDialog().findViewById(R.id.vendor);
        version = (TextView) getDialog().findViewById(R.id.version);
        maxRange = (TextView) getDialog().findViewById(R.id.maximumRange);
        resolution = (TextView) getDialog().findViewById(R.id.resolution);
        power = (TextView) getDialog().findViewById(R.id.power);
        minDelay = (TextView) getDialog().findViewById(R.id.minimumDelay);
    }

    private void initTextValues() {
        Integer sensorType = getArguments().getInt("sensorType");
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(sensorType);
        name.setText(sensor.getName());
        vendor.setText(sensor.getVendor());
        version.setText(String.valueOf(sensor.getVersion()));
        maxRange.setText(String.valueOf(sensor.getMaximumRange()));
        resolution.setText(String.valueOf(sensor.getResolution()));
        power.setText(String.valueOf(sensor.getPower()));
        minDelay.setText(String.valueOf(sensor.getMinDelay()));
    }
}
