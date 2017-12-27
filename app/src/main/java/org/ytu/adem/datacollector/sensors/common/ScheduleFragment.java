package org.ytu.adem.datacollector.sensors.common;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import org.ytu.adem.datacollector.R;
import org.ytu.adem.datacollector.enums.Action;
import org.ytu.adem.datacollector.model.RecordLength;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
    private int sensorType;
    private String configFileName;

    public ScheduleFragment() {

    }

    @SuppressLint("ValidFragment")
    public ScheduleFragment(int sensorType, String configFileName) {
        this.sensorType = sensorType;
        this.configFileName = configFileName;
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
        final CheckBox active = (CheckBox) v.findViewById(R.id.active);
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
        initScheduleInfo(active.isChecked());
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

    private void initEditTextListener(final EditText editText) {
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog(editText).show();
            }
        });
    }

    private void clickActiveCheckbox(Spinner recordFrequency, EditText recordLengthHour, EditText recordLengthMinute) {
        CheckBox checkbox = (CheckBox) v.findViewById(R.id.active);

        if (checkbox.isChecked()) {
            if (checkRequiredFields(recordLengthHour, recordLengthMinute)) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(getString(R.string.shared_preferences_startTime), startTime.getText().toString());
                editor.putInt(getString(R.string.shared_preferences_recordLength), getRecordLengthValue(Integer.valueOf(recordLengthHour.getText().toString()), Integer.valueOf(recordLengthMinute.getText().toString())));
                editor.commit();
                changeScheduleStatus(true, startTime, recordFrequency, recordLengthHour, recordLengthMinute);
            } else {
                checkbox.setChecked(false);
            }
        } else {
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

}
