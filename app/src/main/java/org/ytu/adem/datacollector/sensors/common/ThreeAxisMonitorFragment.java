package org.ytu.adem.datacollector.sensors.common;


import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.ytu.adem.datacollector.R;
import org.ytu.adem.datacollector.model.ThreeAxisValue;
import org.ytu.adem.datacollector.util.Util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by Adem on 15.06.2017.
 */

public class ThreeAxisMonitorFragment extends Fragment implements SensorEventListener {
    private static final int ONE_SECOND_NANO = 1000000000;
    private static final int LINE_WIDTH = 5;
    List<String> valuesToWrite = new ArrayList<>();
    SharedPreferences preferences;
    private SensorManager sensorManager;
    private Sensor activeSensor;
    private List<Entry> xEntries = new ArrayList<Entry>();
    private List<Entry> yEntries = new ArrayList<>();
    private List<Entry> zEntries = new ArrayList<>();
    private LineChart chart;
    private long lastUpdate = 0;
    private Button stopButton;
    private int precision;
    private int frequency;
    private String configFileName;
    private int sensorType;
    private String fileHeaderText;

    public ThreeAxisMonitorFragment() {

    }

    @SuppressLint("ValidFragment")
    public ThreeAxisMonitorFragment(int sensorType, String configFileName) {
        this.sensorType = sensorType;
        this.configFileName = configFileName;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    private void startRecordSensorData(View view) {
        activeSensor = sensorManager.getDefaultSensor(this.sensorType);
        frequency = preferences.getInt(getString(R.string.shared_preferences_frequency), 1);
        precision = preferences.getInt(getString(R.string.shared_preferences_precision), 2);
        fileHeaderText = Util.prepareFileHeader(preferences.getString(getString(R.string.shared_preferences_sensorName), "Bilinmeyen"), frequency, precision);
        sensorManager.registerListener(this, activeSensor,
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
        return inflater.inflate(R.layout.fragment_3axis_monitor, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        preferences = getContext().getSharedPreferences(this.configFileName, MODE_PRIVATE);

        chart = (LineChart) getActivity().findViewById(R.id.accelerometerChart);
        Button startButton = (Button) getActivity().findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
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
        File path = getContext().getExternalFilesDir(this.configFileName + "/");
        //TODO: Dosya adını sharedPrefences'dan al.
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM HH:mm:ss");

        String fileName = preferences.getString(getString(R.string.shared_preferences_fileName), "a") + "(" + sdf.format(new Date()) + ").txt";
        try {
            File file = new File(path, fileName);
            file.setReadOnly();
            FileWriter writer = new FileWriter(new File(path, fileName));

            StringBuilder sb = new StringBuilder();
            sb.append(fileHeaderText);
            for (String line : valuesToWrite) {
                sb.append(line + "\n");
            }
            writer.write(sb.toString());
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
        if (actualTime - lastUpdate < ONE_SECOND_NANO / frequency) return;

        lastUpdate = actualTime;
        xEntries.add(new Entry(xEntries.size(), event.values[0]));
        yEntries.add(new Entry(xEntries.size(), event.values[1]));
        zEntries.add(new Entry(xEntries.size(), event.values[2]));
        //
        SimpleDateFormat sdf = new SimpleDateFormat(preferences.getString("dateFormat", "yyyy-MM"));
        String valuesWithDate = sdf.format(new Date()) + " || " +
                new ThreeAxisValue(
                        Util.formatFloatValueByPrecision(event.values[0], precision),
                        Util.formatFloatValueByPrecision(event.values[1], precision),
                        Util.formatFloatValueByPrecision(event.values[2], precision)).toString();
        valuesToWrite.add(valuesWithDate);
        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        LineDataSet dataSetX = new LineDataSet(xEntries, "0");
        dataSetX.setColor(Color.BLUE);
        dataSetX.setValueTextColor(Color.BLACK);
        dataSetX.setLineWidth(LINE_WIDTH);
        dataSetX.setDrawCircles(false);
        LineDataSet dataSetY = new LineDataSet(yEntries, String.valueOf(activeSensor.getMinDelay()));
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

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

}
