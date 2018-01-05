package org.ytu.adem.datacollector.sensors.common;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import org.ytu.adem.datacollector.R;
import org.ytu.adem.datacollector.enums.Action;
import org.ytu.adem.datacollector.model.RecordLength;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Adem on 20.08.2017.
 */

public class ScheduleFragment extends Fragment {

    private SharedPreferences preferences;
    private View v;
    private AlarmManager alarmManager;
    private Intent intentAlarm;
    private EditText startTime;
    private EditText sensorList;
    private int sensorType;
    private String configFileName;
    private boolean isMultiple;
    private AlertDialog multipleSelectionDialog;
    private List<Sensor> activeSensorList = new ArrayList<>();
    private Map<Integer, String> selectedSensors = new HashMap<>();

    public ScheduleFragment() {

    }

    @SuppressLint("ValidFragment")
    public ScheduleFragment(int sensorType, String configFileName, boolean isMultiple) {
        this.sensorType = sensorType;
        this.configFileName = configFileName;
        this.isMultiple = isMultiple;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getContext().getSharedPreferences(this.configFileName, MODE_PRIVATE);
        intentAlarm = new Intent(this.getActivity(), Receiver.class);
    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_schedule, container, false);
        startTime = (EditText) v.findViewById(R.id.startTime);
        final Spinner recordFrequency = (Spinner) v.findViewById(R.id.recordFrequency);
        final EditText recordLengthHour = (EditText) v.findViewById(R.id.recordLengthHour);
        final EditText recordLengthMinute = (EditText) v.findViewById(R.id.recordLengthMinute);
        final Switch active = (Switch) v.findViewById(R.id.active);
        startTime.setText(preferences.getString(getResources().getString(R.string.shared_preferences_startTime), null));
        recordLengthHour.setText(String.valueOf(getRecordLength().getHour()));
        recordLengthMinute.setText(String.valueOf(getRecordLength().getMinute()));
        active.setChecked(preferences.getBoolean(getString(R.string.shared_preferences_scheduleActive), false));
        changeScheduleStatus(active.isChecked(), startTime, recordFrequency, recordLengthHour, recordLengthMinute);
        active.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickActiveCheckbox(recordFrequency, recordLengthHour, recordLengthMinute);
            }
        });
        initEditTextListener(startTime);
        initRecordFrequencies();
        if (isMultiple) sensorList = (EditText) v.findViewById(R.id.sensorList);
        initScheduleInfo(active.isChecked());
        if (this.isMultiple) {
            List<Integer> selectedSensorIds = loadItems();
            initMultipleSensorListEditText(selectedSensorIds);
            selectedSensors = getSelectedSensors(selectedSensorIds);
        }
        return v;
    }

    private RecordLength getRecordLength() {
        int recordLengthValue = preferences.getInt(getString(R.string.shared_preferences_recordLength), 0);
        RecordLength recordLength = new RecordLength();
        recordLength.setHour(recordLengthValue / 60);
        recordLength.setMinute(recordLengthValue % 60);
        return recordLength;
    }

    private int getRecordLengthValue(int hour, int minute) {
        return hour * 60 + minute;
    }

    private void initRecordFrequencies() {
        Spinner recordFrequency = (Spinner) v.findViewById(R.id.recordFrequency);
        recordFrequency.setEnabled(false);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.schedule_frequencies, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        recordFrequency.setAdapter(adapter);
    }

    private void initScheduleInfo(boolean isActivated) {
        TextView scheduleInfo = (TextView) v.findViewById(R.id.scheduleInfo);
        Spinner recordFrequency = (Spinner) v.findViewById(R.id.recordFrequency);
        EditText startTime = (EditText) v.findViewById(R.id.startTime);
        EditText recordLengthHour = (EditText) v.findViewById(R.id.recordLengthHour);
        EditText recordLengthMinute = (EditText) v.findViewById(R.id.recordLengthMinute);
        if (isActivated) {
            scheduleInfo.setText(getString(R.string.schedule_activated, startTime.getText(), recordFrequency.getSelectedItem().toString(), recordLengthHour.getText(), recordLengthMinute.getText()));
        } else if (checkRequiredFields(recordLengthHour, recordLengthMinute)) {
            scheduleInfo.setText(getResources().getString(R.string.schedule_not_activated));
        } else if (recordLengthHour.getText().length() > 0) {
            scheduleInfo.setText(getString(R.string.schedule_fill_required_fields));
        }
    }

    private void initMultipleSensorListEditText(List<Integer> selectedSensorIds) {
        sensorList.setVisibility(View.VISIBLE);
        if (!selectedSensorIds.isEmpty()) {
            int i = 0;
            do {
                if (sensorList.getText().length() == 0) {
                    sensorList.setText(findSensorNameByType(selectedSensorIds.get(i)));
                } else {
                    sensorList.setText(sensorList.getText() + "\n" + findSensorNameByType(selectedSensorIds.get(i)));
                }
                i++;
            } while (i < selectedSensorIds.size());
        }
        multipleSelectionDialog = createMultipleSensorSelectDialog();
        sensorList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMultipleSelectionDialog();
            }
        });
    }

    private void openMultipleSelectionDialog() {
        List<Integer> items = loadItems();
        multipleSelectionDialog.show();
        uncheckAllItems();
        for (int item : items) {
            multipleSelectionDialog.getListView().setItemChecked(item, true);
        }
    }

    private boolean saveItems(List<Integer> items) {
        String preferenceName = "selectedSensors";
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(preferenceName + "_size", items.size());
        for (int i = 0; i < items.size(); i++)
            editor.putInt(preferenceName + "_" + i, items.get(i));
        return editor.commit();
    }

    public void uncheckAllItems() {
        int numberOfItems = multipleSelectionDialog.getListView().getCheckedItemPositions().size();
        for (int i = 0; i < numberOfItems; i++) {
            multipleSelectionDialog.getListView().setItemChecked(i, false);
        }
    }

    public List<Integer> loadItems() {
        String preferenceName = "selectedSensors";
        int size = preferences.getInt(preferenceName + "_size", 0);
        List<Integer> items = new ArrayList<>();
        for (int i = 0; i < size; i++)
            items.add(preferences.getInt(preferenceName + "_" + i, 0));
        return items;
    }

    private AlertDialog createMultipleSensorSelectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Sensörleri Seç");
        activeSensorList = getActiveSensorList();
        builder.setMultiChoiceItems(getSensorNamesArray(activeSensorList), null, null);
        builder.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                acceptSensorSelection();
            }
        });
        AlertDialog dialog = builder.create();

        return dialog;
    }

    private void acceptSensorSelection() {
        List<Integer> selectedItems = new ArrayList<>();
        boolean hasSelectedItem = false;
        SparseBooleanArray checkedItems = multipleSelectionDialog.getListView().getCheckedItemPositions();
        for (int i = 0; i < checkedItems.size(); i++) {
            if (checkedItems.valueAt(i)) {
                hasSelectedItem = true;
                String sensorName = findSensorNameByType(activeSensorList.get(i).getType());
                if (selectedItems.isEmpty()) {
                    sensorList.setText(sensorName, TextView.BufferType.EDITABLE);
                } else {
                    sensorList.setText(sensorList.getText() + "\n" + sensorName);
                }
                selectedItems.add(activeSensorList.get(i).getType());

            }
        }
        if (!hasSelectedItem) sensorList.setText(null);
        saveItems(selectedItems);
        selectedSensors = getSelectedSensors(selectedItems);
    }

    private Map<Integer, String> getSelectedSensors(List<Integer> sensorTypeIds) {
        Map<Integer, String> selectedSensors = new HashMap<>();
        for (Integer sensorType : sensorTypeIds) {
            selectedSensors.put(sensorType, findConfigFileNameByType(sensorType));
        }
        return selectedSensors;
    }

    private String[] getSensorNamesArray(List<Sensor> sensorList) {
        List<String> sensorNameList = new ArrayList();
        for (Sensor sensor : sensorList) {
            String sensorName = findSensorNameByType(sensor.getType());
            if (sensorName != null) {
                sensorNameList.add(sensorName);
            }
        }
        return sensorNameList.toArray(new String[0]);
    }

    private List<Sensor> getActiveSensorList() {
        SensorManager sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        activeSensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
        List<Sensor> newSensorList = new ArrayList<>();
        for (Sensor sensor : activeSensorList) {
            String sensorName = findSensorNameByType(sensor.getType());
            if (sensorName != null) {
                newSensorList.add(sensor);
            }
        }
        return newSensorList;
    }

    private void initEditTextListener(final EditText editText) {
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog(editText).show();
            }
        });
    }

    private void clickActiveCheckbox(Spinner recordFrequency, EditText recordLengthHour, EditText recordLengthMinute) {
        Switch checkbox = (Switch) v.findViewById(R.id.active);

        if (checkbox.isChecked()) {
            if (checkRequiredFields(recordLengthHour, recordLengthMinute)) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(getString(R.string.shared_preferences_startTime), startTime.getText().toString());
                editor.putInt(getString(R.string.shared_preferences_recordLength), getRecordLengthValue(Integer.valueOf(recordLengthHour.getText().toString()), Integer.valueOf(recordLengthMinute.getText().toString())));
                editor.commit();
                changeScheduleStatus(true, startTime, recordFrequency, recordLengthHour, recordLengthMinute);
                checkbox.setText(getString(R.string.schedule_active));
            } else {
                checkbox.setChecked(false);
                checkbox.setText(getString(R.string.schedule_passive));
            }
        } else {
            checkbox.setText(getString(R.string.schedule_passive));
            changeScheduleStatus(false, startTime, recordFrequency, recordLengthHour, recordLengthMinute);
        }
        initScheduleInfo(checkbox.isChecked());
    }

    private void changeScheduleStatus(boolean activated, EditText startTime, Spinner recordFrequency, EditText recordLengthHour, EditText recordLengthMinute) {
        SharedPreferences.Editor editor = preferences.edit();
        if (activated) {
            editor.putBoolean(getResources().getString(R.string.shared_preferences_scheduleActive), activated);
            scheduleAlarm();
        } else {
            editor.remove("scheduleActive");
            cancelAlarm();
        }
        editor.commit();
        startTime.setEnabled(!activated);
        recordFrequency.setEnabled(!activated);
        recordLengthHour.setEnabled(!activated);
        recordLengthMinute.setEnabled(!activated);
    }

    private boolean checkRequiredFields(EditText recordLengthHour, EditText recordLengthMinute) {
        TextInputLayout startLabel = (TextInputLayout) v.findViewById(R.id.startTimeLabel);
        TextInputLayout hourLabel = (TextInputLayout) v.findViewById(R.id.recordLengthHourLabel);
        TextInputLayout minuteLabel = (TextInputLayout) v.findViewById(R.id.recordLengthMinuteLabel);
        TextInputLayout sensorListLabel = (TextInputLayout) v.findViewById(R.id.sensorListLabel);
        boolean hasError = false;
        if (startTime.getText().toString().isEmpty()) {
            startLabel.setError(getString(R.string.error_required));
            hasError = true;
        } else {
            startLabel.setError(null);
        }
        if (recordLengthHour.getText().toString().isEmpty()) {
            hourLabel.setError(getString(R.string.error_required));
            hasError = true;
        } else {
            hourLabel.setError(null);
        }
        if (recordLengthMinute.getText().toString().isEmpty()) {
            minuteLabel.setError(getString(R.string.error_required));
            hasError = true;
        } else {
            minuteLabel.setError(null);
        }
        if (isMultiple) {
            if (sensorList.getText().toString().isEmpty()) {
                sensorListLabel.setError(getString(R.string.error_required));
                hasError = true;
            } else {
                sensorListLabel.setError(null);
            }
        }
        return !hasError;
    }

    private void cancelAlarm() {
        PendingIntent startingPendingIntent = PendingIntent.getBroadcast(this.getActivity(),
                this.sensorType,
                intentAlarm, PendingIntent.FLAG_ONE_SHOT);
        PendingIntent stoppingPendingIntent = PendingIntent.getBroadcast(this.getActivity(),
                100 - this.sensorType,
                intentAlarm, PendingIntent.FLAG_ONE_SHOT);
        alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        if (startingPendingIntent != null) {
            startingPendingIntent.cancel();
            alarmManager.cancel(startingPendingIntent);
        }
        if (stoppingPendingIntent != null) {
            stoppingPendingIntent.cancel();
            alarmManager.cancel(stoppingPendingIntent);
        }
    }

    private void scheduleAlarm() {
        Map<String, Integer> timeMap = getTimeHourAndMinute(startTime.getText().toString());
        int alarmHour = timeMap.get("hour");
        int alarmMinute = timeMap.get("minute");
        Date currentDate = new Date();//initializes to now
        Calendar cal_alarm = Calendar.getInstance();
        Calendar cal_now = Calendar.getInstance();
        cal_now.setTime(currentDate);
        cal_alarm.setTime(currentDate);
        cal_alarm.set(Calendar.HOUR_OF_DAY, alarmHour);//set the alarm time
        cal_alarm.set(Calendar.MINUTE, alarmMinute);
        cal_alarm.set(Calendar.SECOND, 0);
        if (cal_alarm.before(cal_now)) {//if its in the past increment
            cal_alarm.add(Calendar.DATE, 1);
        }
        // create an Intent and set the class which will execute when Alarm triggers, here we have
        // given AlarmReciever in the Intent, the onRecieve() method of this class will execute when
        // alarm triggers and
        //we will write the code to send SMS inside onRecieve() method pf Alarmreciever class
        intentAlarm.putExtra("sensorType", sensorType);
        intentAlarm.putExtra("action", Action.START.toString());
        if (isMultiple) intentAlarm.putExtra("selectedSensors", (Serializable) selectedSensors);
        alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        //set the alarm for particular time

        alarmManager.set(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), PendingIntent.getBroadcast(this.getActivity(),
                sensorType,
                intentAlarm, PendingIntent.FLAG_ONE_SHOT));
        intentAlarm.putExtra("action", Action.STOP.toString());
        cal_alarm.add(Calendar.MINUTE, preferences.getInt(getString(R.string.shared_preferences_recordLength), 0));
        alarmManager.set(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), PendingIntent.getBroadcast(this.getActivity(),
                100 - sensorType,
                intentAlarm, PendingIntent.FLAG_ONE_SHOT));
    }

    private Dialog createDialog(EditText editText) {
        int hour;
        int minute;
        if (startTime.getText().length() > 0) {
            Map<String, Integer> timeMap = getTimeHourAndMinute(startTime.getText().toString());
            hour = timeMap.get("hour");
            minute = timeMap.get("minute");
        } else {
            hour = 1;
            minute = 1;
        }
        return new TimePickerDialog(getActivity(), getTimePickerListener(editText), hour, minute, true);
    }

    private TimePickerDialog.OnTimeSetListener getTimePickerListener(final EditText editText) {
        return new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay,
                                  int minute) {
                String timeText = String.format("%02d:%02d", hourOfDay, minute);
                editText.setText(timeText, TextView.BufferType.EDITABLE);
            }
        };
    }

    private Map<String, Integer> getTimeHourAndMinute(String time) {
        Map<String, Integer> timeMap = new HashMap<>();
        if (time.isEmpty()) return timeMap;
        String[] timeParts = time.split(":");
        timeMap.put("hour", Integer.parseInt(timeParts[0]));
        timeMap.put("minute", Integer.parseInt(timeParts[1]));
        return timeMap;
    }

    private String findSensorNameByType(int sensorType) {
        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                return getString(R.string.sensor_accelerometer);
            case Sensor.TYPE_LINEAR_ACCELERATION:
                return getString(R.string.sensor_linear_acceleration);
            case Sensor.TYPE_GRAVITY:
                return getString(R.string.sensor_gravity);
            case Sensor.TYPE_GYROSCOPE:
                return getString(R.string.sensor_gyroscope);
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                return getString(R.string.sensor_relative_humidity);
            case Sensor.TYPE_LIGHT:
                return getString(R.string.sensor_light);
            case Sensor.TYPE_MAGNETIC_FIELD:
                return getString(R.string.sensor_magnetic_field);
            case Sensor.TYPE_PRESSURE:
                return getString(R.string.sensor_pressure);
            case Sensor.TYPE_PROXIMITY:
                return getString(R.string.sensor_proximity);
            case Sensor.TYPE_ROTATION_VECTOR:
                return getString(R.string.sensor_rotation_vector);
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                return getString(R.string.sensor_ambient_temperature);
            case Sensor.TYPE_ALL:
                return getString(R.string.sensor_multiple);
            default:
        }
        return null;
    }

    private String findConfigFileNameByType(int sensorType) {
        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                return getString(R.string.accelerometer_config_fileName);
            case Sensor.TYPE_LINEAR_ACCELERATION:
                return getString(R.string.accelerometer_config_fileName);
            case Sensor.TYPE_GRAVITY:
                return getString(R.string.gravity_config_fileName);
            case Sensor.TYPE_GYROSCOPE:
                return getString(R.string.gyroscope_config_fileName);
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                return getString(R.string.humidity_config_fileName);
            case Sensor.TYPE_LIGHT:
                return getString(R.string.light_config_fileName);
            case Sensor.TYPE_MAGNETIC_FIELD:
                return getString(R.string.magnetic_field_config_fileName);
            case Sensor.TYPE_PRESSURE:
                return getString(R.string.pressure_config_fileName);
            case Sensor.TYPE_PROXIMITY:
                return getString(R.string.proximity_config_fileName);
            case Sensor.TYPE_ROTATION_VECTOR:
                return getString(R.string.rotation_config_fileName);
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                return getString(R.string.temperature_config_fileName);
            case Sensor.TYPE_ALL:
                return getString(R.string.multiple_config_fileName);
            default:
        }
        return null;
    }

}
