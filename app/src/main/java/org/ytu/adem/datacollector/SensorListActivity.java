package org.ytu.adem.datacollector;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import org.ytu.adem.datacollector.sensors.acceleration.AccelerationActivity;
import org.ytu.adem.datacollector.sensors.accelerometer.AccelerometerActivity;
import org.ytu.adem.datacollector.sensors.gravity.GravityActivity;
import org.ytu.adem.datacollector.sensors.humidity.HumidityActivity;
import org.ytu.adem.datacollector.sensors.light.LightActivity;
import org.ytu.adem.datacollector.sensors.magneticField.MagneticFieldActivity;
import org.ytu.adem.datacollector.sensors.pressure.PressureActivity;
import org.ytu.adem.datacollector.sensors.proximity.ProximityActivity;
import org.ytu.adem.datacollector.sensors.rotationVector.RotationVectorActivity;
import org.ytu.adem.datacollector.sensors.temperature.TemperatureActivity;

public class SensorListActivity extends AppCompatActivity implements View.OnClickListener {
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_accelerometer:
                openAccelerometerActivity(view);
                break;
            case R.id.btn_temperature:
                openTemperatureActivity(view);
                break;
            case R.id.btn_gravity:
                openGravityActivity(view);
                break;
            case R.id.btn_gyroscope:
                openAccelerationActivity(view);
                break;
            default:
                break;
        }
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
        Button accelerometerButton = (Button) findViewById(R.id.btn_accelerometer);
        accelerometerButton.setOnClickListener(this);
        isSensorButtonEnabled(accelerometerSensor, accelerometerButton);
        Button temperatureButton = (Button) findViewById(R.id.btn_temperature);
        temperatureButton.setOnClickListener(this);
        isSensorButtonEnabled(temperatureSensor, (Button) findViewById(R.id.btn_temperature));
        isSensorButtonEnabled(gravitySensor, (Button) findViewById(R.id.btn_gravity));
        isSensorButtonEnabled(gyroscopeSensor, (Button) findViewById(R.id.btn_gyroscope));
        isSensorButtonEnabled(lightSensor, (Button) findViewById(R.id.btn_light));
        isSensorButtonEnabled(accelerationSensor, (Button) findViewById(R.id.btn_linear_acceleration));
        isSensorButtonEnabled(magneticFieldSensor, (Button) findViewById(R.id.btn_magnetic_field));
        isSensorButtonEnabled(pressureSensor, (Button) findViewById(R.id.btn_pressure));
        isSensorButtonEnabled(proximitySensor, (Button) findViewById(R.id.btn_proximity));
        isSensorButtonEnabled(humiditySensor, (Button) findViewById(R.id.btn_relative_humidity));
        isSensorButtonEnabled(rotationVectorSensor, (Button) findViewById(R.id.btn_rotation_vector));
    }

    private void isSensorButtonEnabled(Sensor sensor, Button button) {
        if(sensor == null) button.setVisibility(View.GONE);
    }

    public void openAccelerometerActivity(View view) {
        Intent accelerometerIntent = new Intent(this, AccelerometerActivity.class);
        startActivity(accelerometerIntent);
    }

    public void openTemperatureActivity(View view) {
        Intent tempreratureIntent = new Intent(this, TemperatureActivity.class);
        startActivity(tempreratureIntent);
    }

    public void openGravityActivity(View view) {
        Intent gravityIntent = new Intent(this, GravityActivity.class);
        startActivity(gravityIntent);
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
