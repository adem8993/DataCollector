package org.ytu.adem.datacollector;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;

import org.ytu.adem.datacollector.sensors.MultipleRecorder;
import org.ytu.adem.datacollector.sensors.acceleration.AccelerationActivity;
import org.ytu.adem.datacollector.sensors.accelerometer.AccelerometerActivity;
import org.ytu.adem.datacollector.sensors.gravity.GravityActivity;
import org.ytu.adem.datacollector.sensors.gravity.GravityRecorder;
import org.ytu.adem.datacollector.sensors.gyroscope.GyroscopeActivity;
import org.ytu.adem.datacollector.sensors.humidity.HumidityActivity;
import org.ytu.adem.datacollector.sensors.light.LightActivity;
import org.ytu.adem.datacollector.sensors.magneticField.MagneticFieldActivity;
import org.ytu.adem.datacollector.sensors.pressure.PressureActivity;
import org.ytu.adem.datacollector.sensors.proximity.ProximityActivity;
import org.ytu.adem.datacollector.sensors.rotationVector.RotationVectorActivity;
import org.ytu.adem.datacollector.sensors.temperature.TemperatureActivity;

import java.util.HashMap;
import java.util.Map;

public class SensorListActivity extends AppCompatActivity {
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private Sensor temperatureSensor;
    private Sensor gravitySensor;
    private Sensor gyroscopeSensor;
    private Sensor lightSensor;
    private Sensor accelerationSensor;
    private Sensor magneticFieldSensor;
    private Sensor pressureSensor;
    private Sensor proximitySensor;
    private Sensor humiditySensor;
    private Sensor rotationVectorSensor;
    private Map<Integer, Boolean> selectedSensors = new HashMap<>();
    private int selectedCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_list);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        initMultipleRecordButton();
        getSensors();
        setButtonStates();
    }

    private void getSensors() {
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        accelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        magneticFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        humiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
    }

    private void setButtonStates() {
        hideSensorCheckboxAndButton(accelerometerSensor, (Button) findViewById(R.id.btn_accelerometer), (CheckBox) findViewById(R.id.check_accelerometer));
        hideSensorCheckboxAndButton(temperatureSensor, (Button) findViewById(R.id.btn_temperature), (CheckBox) findViewById(R.id.check_temperature));
        hideSensorCheckboxAndButton(gravitySensor, (Button) findViewById(R.id.btn_gravity), (CheckBox) findViewById(R.id.check_gravity));
        hideSensorCheckboxAndButton(gyroscopeSensor, (Button) findViewById(R.id.btn_gyroscope), (CheckBox) findViewById(R.id.check_gyroscope));
        hideSensorCheckboxAndButton(lightSensor, (Button) findViewById(R.id.btn_light), (CheckBox) findViewById(R.id.check_light));
        hideSensorCheckboxAndButton(accelerationSensor, (Button) findViewById(R.id.btn_linear_acceleration), (CheckBox) findViewById(R.id.check_linear_acceleration));
        hideSensorCheckboxAndButton(magneticFieldSensor, (Button) findViewById(R.id.btn_magnetic_field), (CheckBox) findViewById(R.id.check_magnetic_field));
        hideSensorCheckboxAndButton(pressureSensor, (Button) findViewById(R.id.btn_pressure), (CheckBox) findViewById(R.id.check_pressure));
        hideSensorCheckboxAndButton(proximitySensor, (Button) findViewById(R.id.btn_proximity), (CheckBox) findViewById(R.id.check_proximity));
        hideSensorCheckboxAndButton(humiditySensor, (Button) findViewById(R.id.btn_relative_humidity), (CheckBox) findViewById(R.id.check_relative_humidity));
        hideSensorCheckboxAndButton(rotationVectorSensor, (Button) findViewById(R.id.btn_rotation_vector), (CheckBox) findViewById(R.id.check_rotation_vector));
    }

    private void initMultipleRecordButton() {
        final AppCompatImageButton recordButton = (AppCompatImageButton) findViewById(R.id.record_button);
        recordButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                ColorStateList stateList = recordButton.getBackgroundTintList();
                int color = stateList.getDefaultColor();
                boolean stopped = color == getColor(android.R.color.holo_red_light);
                toggleRecordButton(recordButton, color == getColor(android.R.color.holo_red_light));
                if (stopped) {
                    stopRecording();
                } else {
                    startRecording();
                }
            }
        });
    }

    private void startRecording() {
        Intent multipleIntent = new Intent(this, MultipleRecorder.class);
        startService(multipleIntent);
    }

    private void stopRecording() {
        Intent multipleIntent = new Intent(this, MultipleRecorder.class);
        stopService(multipleIntent);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void toggleRecordButton(AppCompatImageButton recordButton, boolean stopped) {
        if (stopped) {
            recordButton.setBackgroundTintList(ColorStateList.valueOf(getColor(android.R.color.holo_green_light)));
            recordButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            enableAllCheckbox();
        } else {
            recordButton.setBackgroundTintList(ColorStateList.valueOf(getColor(android.R.color.holo_red_light)));
            recordButton.setImageResource(R.drawable.ic_stop_black_24dp);
            disableAllCheckbox();
        }
    }

    private void hideSensorCheckboxAndButton(Sensor sensor, Button button, CheckBox checkbox) {
        if (!isSensorAvailable(sensor)) {
            button.setVisibility(View.GONE);
            checkbox.setVisibility(View.GONE);
        }
    }

    private boolean isSensorAvailable(Sensor sensor) {
        if (sensor == null) return false;
        return true;
    }

    public void openAccelerometerActivity(View view) {
        Intent accelerometerIntent = new Intent(this, AccelerometerActivity.class);
        startActivity(accelerometerIntent);
    }

    private void disableAllCheckbox() {
        ((CheckBox) findViewById(R.id.check_accelerometer)).setEnabled(false);
        ((CheckBox) findViewById(R.id.check_rotation_vector)).setEnabled(false);
        ((CheckBox) findViewById(R.id.check_relative_humidity)).setEnabled(false);
        ((CheckBox) findViewById(R.id.check_proximity)).setEnabled(false);
        ((CheckBox) findViewById(R.id.check_pressure)).setEnabled(false);
        ((CheckBox) findViewById(R.id.check_magnetic_field)).setEnabled(false);
        ((CheckBox) findViewById(R.id.check_light)).setEnabled(false);
        ((CheckBox) findViewById(R.id.check_gyroscope)).setEnabled(false);
        ((CheckBox) findViewById(R.id.check_gravity)).setEnabled(false);
        ((CheckBox) findViewById(R.id.check_temperature)).setEnabled(false);
        ((CheckBox) findViewById(R.id.check_linear_acceleration)).setEnabled(false);
    }


    private void enableAllCheckbox() {
        ((CheckBox) findViewById(R.id.check_accelerometer)).setEnabled(true);
        ((CheckBox) findViewById(R.id.check_rotation_vector)).setEnabled(true);
        ((CheckBox) findViewById(R.id.check_relative_humidity)).setEnabled(true);
        ((CheckBox) findViewById(R.id.check_proximity)).setEnabled(true);
        ((CheckBox) findViewById(R.id.check_pressure)).setEnabled(true);
        ((CheckBox) findViewById(R.id.check_magnetic_field)).setEnabled(true);
        ((CheckBox) findViewById(R.id.check_light)).setEnabled(true);
        ((CheckBox) findViewById(R.id.check_gyroscope)).setEnabled(true);
        ((CheckBox) findViewById(R.id.check_gravity)).setEnabled(true);
        ((CheckBox) findViewById(R.id.check_temperature)).setEnabled(true);
        ((CheckBox) findViewById(R.id.check_linear_acceleration)).setEnabled(true);
    }

    public void clickCheckBox(View view) {
        if (((CheckBox) view).isChecked()) {
            selectedCount++;
        } else {
            selectedCount--;
        }
        AppCompatImageButton recordButton = (AppCompatImageButton) this.findViewById(R.id.record_button);
        if (selectedCount > 1) {
            recordButton.setVisibility(View.VISIBLE);
        } else {
            recordButton.setVisibility(View.INVISIBLE);
        }
    }

    public void openTemperatureActivity(View view) {
        Intent tempreratureIntent = new Intent(this, TemperatureActivity.class);
        startActivity(tempreratureIntent);
    }

    public void openGravityActivity(View view) {
        Intent gravityIntent = new Intent(this, GravityActivity.class);
        startActivity(gravityIntent);
    }

    public void openGyroscopeActivity(View view) {
        Intent gyroscopeIntent = new Intent(this, GyroscopeActivity.class);
        startActivity(gyroscopeIntent);
    }

    public void openAccelerationActivity(View view) {
        Intent accelarationIntent = new Intent(this, AccelerationActivity.class);
        startActivity(accelarationIntent);
    }

    public void openLightActivity(View view) {
        Intent lightIntent = new Intent(this, LightActivity.class);
        startActivity(lightIntent);
    }

    public void openMagneticFieldActivity(View view) {
        Intent magneticFieldIntent = new Intent(this, MagneticFieldActivity.class);
        startActivity(magneticFieldIntent);
    }

    public void openPressureActivity(View view) {
        Intent pressureIntent = new Intent(this, PressureActivity.class);
        startActivity(pressureIntent);
    }

    public void openProximityActivity(View view) {
        Intent proximityIntent = new Intent(this, ProximityActivity.class);
        startActivity(proximityIntent);
    }

    public void openHumidityActivity(View view) {
        Intent humidityIntent = new Intent(this, HumidityActivity.class);
        startActivity(humidityIntent);
    }

    public void openRotationVectorActivity(View view) {
        Intent rotationVectorIntent = new Intent(this, RotationVectorActivity.class);
        startActivity(rotationVectorIntent);
    }
}
