package org.ytu.adem.datacollector.sensors.accelerometer;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.ytu.adem.datacollector.R;
import org.ytu.adem.datacollector.model.ThreeAxisValue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by Adem on 15.06.2017.
 */

public class MonitorFragment extends Fragment implements SensorEventListener {
    private static final int ONE_SECOND_NANO = 1000000000;
    private static final int LINE_WIDTH = 5;
    List<String> valuesToWrite = new ArrayList<>();
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private List<Entry> xEntries = new ArrayList<Entry>();
    private List<Entry> yEntries = new ArrayList<>();
    private List<Entry> zEntries = new ArrayList<>();
    private LineChart chart;
    private long lastUpdate = 0;
    SharedPreferences preferences;
    private Button stopButton;

    public MonitorFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
    }

    private void startRecordSensorData(View view) {
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometerSensor,
                SensorManager.SENSOR_DELAY_GAME);
        stopButton.setEnabled(true);

    }

    private void stopRecordSensorData(View view) {
        writeSensorDataToFile();
        resetSensorData();
        chart.invalidate();
        chart.clear();
        stopButton.setEnabled(false);
        sensorManager.unregisterListener(this);
    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_accelerometer_monitor, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        preferences = getContext().getSharedPreferences(getResources().getString(R.string.accelerometer_config_fileName), MODE_PRIVATE);

        chart = (LineChart) getActivity().findViewById(R.id.accelerometerChart);
        Button startButton = (Button) getActivity().findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecordSensorData(v);
            }
        });
        stopButton = (Button) getActivity().findViewById(R.id.stopButton);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecordSensorData(v);
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this);
    }

    private void writeSensorDataToFile() {
        //TODO: External veya internal storage'a yazma desteği olmalı.
        File path = getContext().getExternalFilesDir(null);
        //TODO: Dosya adını sharedPrefences'dan al.
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM HH:mm:ss");

        String fileName = preferences.getString(getString(R.string.shared_preferences_fileName), "a") + "(" + sdf.format(new Date()) + ").txt";
        try {
            FileWriter writer = new FileWriter(new File(path, fileName));
            for (String line : valuesToWrite) {
                writer.write(line + "\n");
            }
            writer.close();
            Toast.makeText(getContext(), "Sensör verileri dosyaya yazıldı.\nDosya adı: " + fileName, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(getContext(), "Veriler dosyaya yazılamadı. Dosya Adı:" + fileName, Toast.LENGTH_SHORT).show();
            System.out.println(e);
        }
    }

    private void resetSensorData() {
        xEntries = new ArrayList<>();
        yEntries = new ArrayList<>();
        zEntries = new ArrayList<>();
        valuesToWrite = new ArrayList<>();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long actualTime = event.timestamp;
        if (actualTime - lastUpdate < ONE_SECOND_NANO / preferences.getInt("frequency", 1)) return;

        lastUpdate = actualTime;
        xEntries.add(new Entry(xEntries.size(), event.values[0]));
        yEntries.add(new Entry(xEntries.size(), event.values[1]));
        zEntries.add(new Entry(xEntries.size(), event.values[2]));
        //
        SimpleDateFormat sdf = new SimpleDateFormat(preferences.getString("dateFormat", "yyyy-MM"));
        valuesToWrite.add(new ThreeAxisValue(sdf.format(new Date()), formatFloatValueByPrecision(event.values[0]), formatFloatValueByPrecision(event.values[1]), formatFloatValueByPrecision(event.values[2])).toString());

        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        LineDataSet dataSetX = new LineDataSet(xEntries, "0");
        dataSetX.setColor(Color.BLUE);
        dataSetX.setValueTextColor(Color.BLACK);
        dataSetX.setLineWidth(LINE_WIDTH);
        dataSetX.setDrawCircles(false);
        LineDataSet dataSetY = new LineDataSet(yEntries, String.valueOf(accelerometerSensor.getMinDelay()));
        dataSetY.setColor(Color.YELLOW);
        dataSetY.setValueTextColor(Color.BLACK);
        dataSetY.setLineWidth(LINE_WIDTH);
        dataSetY.setDrawCircles(false);
        LineDataSet dataSetZ = new LineDataSet(zEntries, "0");
        dataSetZ.setColor(Color.RED);
        dataSetZ.setValueTextColor(Color.BLACK);
        dataSetZ.setLineWidth(LINE_WIDTH);
        dataSetZ.setDrawCircles(false);
        dataSets.add(dataSetX);
        dataSets.add(dataSetY);
        dataSets.add(dataSetZ);
        LineData data = new LineData(dataSets);
        chart.setData(data);
        chart.invalidate();
    }

    private String formatFloatValueByPrecision(float value) {
        NumberFormat formatter = NumberFormat.getInstance();
        formatter.setMaximumFractionDigits(preferences.getInt("precision", 2));
        return formatter.format(value);
    }

    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "Dynamic Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());
        set.setCircleColor(Color.WHITE);
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void writeToFile(Context context, List<String> textList, boolean writeToExternal) {
        File path;
        if (writeToExternal) {
            path = context.getExternalFilesDir(null);
        } else {
            path = context.getFilesDir();
        }
        try {
            FileWriter writer = new FileWriter(new File(path, "a.txt"));
            for (String line : textList) {
                writer.write(line);
            }
            writer.close();
        } catch (IOException e) {

        }
    }
}
