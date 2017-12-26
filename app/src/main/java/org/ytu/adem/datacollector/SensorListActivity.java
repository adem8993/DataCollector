package org.ytu.adem.datacollector;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;

import org.ytu.adem.datacollector.sensors.acceleration.AccelerationActivity;
import org.ytu.adem.datacollector.sensors.accelerometer.AccelerometerActivity;
import org.ytu.adem.datacollector.sensors.gravity.GravityActivity;
import org.ytu.adem.datacollector.sensors.gyroscope.GyroscopeActivity;
import org.ytu.adem.datacollector.sensors.humidity.HumidityActivity;
import org.ytu.adem.datacollector.sensors.light.LightActivity;
import org.ytu.adem.datacollector.sensors.magneticField.MagneticFieldActivity;
import org.ytu.adem.datacollector.sensors.pressure.PressureActivity;
import org.ytu.adem.datacollector.sensors.proximity.ProximityActivity;
import org.ytu.adem.datacollector.sensors.rotationVector.RotationVectorActivity;
import org.ytu.adem.datacollector.sensors.temperature.TemperatureActivity;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_list);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
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

    public void clickAccelerometerCheck(View view) {
        CheckBox checkbox = (CheckBox) view.findViewById(R.id.check_accelerometer);
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

    public void performFileSearch() {
        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        startActivityForResult(Intent.createChooser(i, "Choose directory"), 9999);
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
