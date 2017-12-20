package org.ytu.adem.datacollector.sensors.accelerometer;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.ytu.adem.datacollector.R;
import org.ytu.adem.datacollector.enums.PendingIntentRequestCode;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Adem on 20.08.2017.
 */

public class ScheduleFragment extends Fragment {

    private SharedPreferences preferences;
    private View v;
    private AlarmManager alarmManager;

    public ScheduleFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getContext().getSharedPreferences(getResources().getString(R.string.accelerometer_config_fileName), MODE_PRIVATE);

    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_accelerometer_schedule, container, false);
        final EditText startTime = (EditText) v.findViewById(R.id.startTime);
        final Spinner recordFrequency = (Spinner) v.findViewById(R.id.recordFrequency);
        final EditText recordLength = (EditText) v.findViewById(R.id.recordLength);
        final CheckBox active = (CheckBox) v.findViewById(R.id.active);
        startTime.setText(preferences.getString(getResources().getString(R.string.shared_preferences_startTime), null));
        active.setChecked(preferences.getBoolean(getResources().getString(R.string.shared_preferences_scheduleActive), false));
        active.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickActiveCheckbox(active.isChecked(), startTime, recordFrequency, recordLength);
            }
        });
    //        initRecordLengthEditTextListener(recordLength);
        initEditTextListener(startTime, getResources().getString(R.string.shared_preferences_startTime));
        initRecordFrequencies(v);
        initScheduleInfo(v, active.isChecked());
        return v;
    }

    private void initRecordLengthEditTextListener(EditText recordLength) {
        recordLength.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt(getResources().getString(R.string.shared_preferences_recordLength), Integer.valueOf(s.toString()));
                    editor.commit();
                }
            }
        });
    }

    private void initRecordFrequencies(View v) {
        Spinner recordFrequency = (Spinner) v.findViewById(R.id.recordFrequency);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.schedule_frequencies, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        recordFrequency.setAdapter(adapter);
    }

    private void initScheduleInfo(View v, boolean isActivated) {
        TextView scheduleInfo = (TextView) v.findViewById(R.id.scheduleInfo);
        EditText recordLength = (EditText) v.findViewById(R.id.recordLength);
        if (isActivated) {
            if (recordLength.getText().length() > 0) {
                scheduleInfo.setText(getResources().getString(R.string.schedule_activate));
            } else {
                scheduleInfo.setText(getResources().getString(R.string.schedule_fill_record_length));
            }
        } else {
            scheduleInfo.setText(getResources().getString(R.string.schedule_not_activated));
        }
    }

    private void initEditTextListener(final EditText editText, final String preferencesName) {
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog(preferencesName, editText).show();
            }
        });
    }

    private void clickActiveCheckbox(boolean isChecked, EditText startTime, Spinner recordFrequency, EditText recordLength) {
        if (isChecked) {
            if (checkRequiredFields(startTime, recordLength)) {
                changeScheduleStatus(false, startTime, recordFrequency, recordLength);
            } else {
                CheckBox checkbox = (CheckBox) v.findViewById(R.id.active);
                checkbox.setChecked(false);
            }
        } else {
            if (isRecordScheduled()) {
                deActivateScheduledRecordConfirmDialog(startTime, recordFrequency, recordLength);
                ;
            } else {
                changeScheduleStatus(false, startTime, recordFrequency, recordLength);
            }
        }
        initScheduleInfo(v, isChecked);
    }

    private boolean isRecordScheduled() {
        Intent intentAlarm = new Intent(this.getActivity(), Receiver.class);
        return (PendingIntent.getBroadcast(this.getActivity(), 0, intentAlarm, PendingIntent.FLAG_NO_CREATE) != null);
    }

    private void changeScheduleStatus(boolean activated, EditText startTime, Spinner recordFrequency, EditText recordLength) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(getResources().getString(R.string.shared_preferences_scheduleActive), activated);
        editor.commit();
        startTime.setEnabled(!activated);
        recordFrequency.setEnabled(!activated);
        recordLength.setEnabled(!activated);
    }

    private void deActivateScheduledRecordConfirmDialog(final EditText startTime, final Spinner recordFrequency, final EditText recordLength) {
        new AlertDialog.Builder(this.getActivity())
                .setTitle("Title")
                .setMessage("Do you really want to whatever?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        changeScheduleStatus(false, startTime, recordFrequency, recordLength);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int whichButton) {
                        CheckBox checkbox = (CheckBox) v.findViewById(R.id.active);
                        checkbox.setChecked(true);
                    }
                }).show();
    }

    private boolean checkRequiredFields(EditText startTime, EditText recordLength) {
        return !startTime.getText().toString().isEmpty() && !recordLength.getText().toString().isEmpty();
    }

    private void cancelAlarm() {
        Intent intentAlarm = new Intent(this.getActivity(), Receiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getActivity(),
                PendingIntentRequestCode.ACCELEROMETER.ordinal(),
                intentAlarm, PendingIntent.FLAG_ONE_SHOT);
        pendingIntent.cancel();
        alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    private void scheduleAlarm() {
        // time at which alarm will be scheduled here alarm is scheduled at 1 day from current time,
        // we fetch  the current time in milliseconds and added 1 day time
        // i.e. 24*60*60*1000= 86,400,000   milliseconds in a day
        Calendar cal = Calendar.getInstance();

        Long time = new GregorianCalendar().getTimeInMillis() + 10 * 1000;

        // create an Intent and set the class which will execute when Alarm triggers, here we have
        // given AlarmReciever in the Intent, the onRecieve() method of this class will execute when
        // alarm triggers and
        //we will write the code to send SMS inside onRecieve() method pf Alarmreciever class
        Intent intentAlarm = new Intent(this.getActivity(), Receiver.class);
        intentAlarm.putExtra("action", getResources().getString(R.string.start));

        // create the object
        alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        //set the alarm for particular time

        alarmManager.set(AlarmManager.RTC_WAKEUP, time, PendingIntent.getBroadcast(this.getActivity(),
                PendingIntentRequestCode.ACCELEROMETER.ordinal(),
                intentAlarm, PendingIntent.FLAG_ONE_SHOT));
        Toast.makeText(this.getContext(), "Alarm Scheduled for Tommrrow", Toast.LENGTH_LONG).show();

    }

    private Dialog createDialog(String preferencesText, EditText editText) {
        return new TimePickerDialog(getActivity(), getTimePickerListener(preferencesText, editText), 1, 1, true);
    }

    private TimePickerDialog.OnTimeSetListener getTimePickerListener(final String preferencesText, final EditText editText) {
        return new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay,
                                  int minute) {
                scheduleAlarm();
                String timeText = String.format("%02d:%02d", hourOfDay, minute);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(preferencesText, timeText);
                editor.commit();
                editText.setText(timeText, TextView.BufferType.EDITABLE);
            }
        };
    }
}
