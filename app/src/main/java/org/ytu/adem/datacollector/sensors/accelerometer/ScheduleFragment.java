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
import org.ytu.adem.datacollector.model.RecordLength;

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
        final EditText recordLengthHour = (EditText) v.findViewById(R.id.recordLengthHour);
        final EditText recordLengthMinute = (EditText) v.findViewById(R.id.recordLengthMinute);
        final CheckBox active = (CheckBox) v.findViewById(R.id.active);
        startTime.setText(preferences.getString(getResources().getString(R.string.shared_preferences_startTime), null));
        recordLengthHour.setText(String.valueOf(getRecordLength().getHour()));
        recordLengthMinute.setText(String.valueOf(getRecordLength().getMinute()));
        active.setChecked(preferences.getBoolean(getResources().getString(R.string.shared_preferences_scheduleActive), false));
        active.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickActiveCheckbox(startTime, recordFrequency, recordLengthHour, recordLengthMinute);
            }
        });
        initEditTextListener(startTime);
        initRecordFrequencies(v);
        initScheduleInfo(v, active.isChecked());
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
        Spinner recordFrequency = (Spinner) v.findViewById(R.id.recordFrequency);
        EditText startTime = (EditText) v.findViewById(R.id.startTime);
        EditText recordLengthHour = (EditText) v.findViewById(R.id.recordLengthHour);
        EditText recordLengthMinute = (EditText) v.findViewById(R.id.recordLengthMinute);
        if (isActivated) {
            scheduleInfo.setText(getString(R.string.schedule_activated, startTime.getText(), recordFrequency.getSelectedItem().toString(), recordLengthHour.getText(), recordLengthMinute.getText()));

        } else if (checkRequiredFields(startTime, recordLengthHour, recordLengthMinute)) {
            scheduleInfo.setText(getResources().getString(R.string.schedule_not_activated));
        } else if (recordLengthHour.getText().length() > 0) {
            scheduleInfo.setText(getResources().getString(R.string.schedule_activate));
        } else {
            scheduleInfo.setText(getResources().getString(R.string.schedule_fill_record_length));
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

    private void clickActiveCheckbox(EditText startTime, Spinner recordFrequency, EditText recordLengthHour, EditText recordLengthMinute) {
        CheckBox checkbox = (CheckBox) v.findViewById(R.id.active);

        if (checkbox.isChecked()) {
            if (checkRequiredFields(startTime, recordLengthHour, recordLengthMinute)) {
                changeScheduleStatus(true, startTime, recordFrequency, recordLengthHour, recordLengthMinute);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(getString(R.string.shared_preferences_startTime), startTime.getText().toString());
                editor.putInt(getString(R.string.shared_preferences_recordLength), getRecordLengthValue(Integer.valueOf(recordLengthHour.getText().toString()), Integer.valueOf(recordLengthMinute.getText().toString())));
                editor.commit();
            } else {
                checkbox.setChecked(false);
            }
        } else {
            if (isRecordScheduled()) {
                deActivateScheduledRecordConfirmDialog(startTime, recordFrequency, recordLengthHour, recordLengthMinute);
                ;
            } else {
                changeScheduleStatus(false, startTime, recordFrequency, recordLengthHour, recordLengthMinute);
            }
        }
        initScheduleInfo(v, checkbox.isChecked());
    }


    private boolean isRecordScheduled() {
        Intent intentAlarm = new Intent(this.getActivity(), Receiver.class);
        return (PendingIntent.getBroadcast(this.getActivity(), 0, intentAlarm, PendingIntent.FLAG_NO_CREATE) != null);
    }

    private void changeScheduleStatus(boolean activated, EditText startTime, Spinner recordFrequency, EditText recordLengthHour, EditText recordLengthMinute) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(getResources().getString(R.string.shared_preferences_scheduleActive), activated);
        editor.commit();
        startTime.setEnabled(!activated);
        recordFrequency.setEnabled(!activated);
        recordLengthHour.setEnabled(!activated);
        recordLengthMinute.setEnabled(!activated);
    }

    private void deActivateScheduledRecordConfirmDialog(final EditText startTime, final Spinner recordFrequency, final EditText recordLengthHour, final EditText recordLengthMinute) {
        new AlertDialog.Builder(this.getActivity())
                .setTitle("Title")
                .setMessage("Do you really want to whatever?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        changeScheduleStatus(false, startTime, recordFrequency, recordLengthHour, recordLengthMinute);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int whichButton) {
                        CheckBox checkbox = (CheckBox) v.findViewById(R.id.active);
                        checkbox.setChecked(true);
                    }
                }).show();
    }

    private boolean checkRequiredFields(EditText startTime, EditText recordLengthHour, EditText recordLengthMinute) {
        return !startTime.getText().toString().isEmpty() && !recordLengthHour.getText().toString().isEmpty() && !recordLengthMinute.getText().toString().isEmpty();
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

    private Dialog createDialog(EditText editText) {
        return new TimePickerDialog(getActivity(), getTimePickerListener(editText), 1, 1, true);
    }

    private TimePickerDialog.OnTimeSetListener getTimePickerListener(final EditText editText) {
        return new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay,
                                  int minute) {
                scheduleAlarm();
                String timeText = String.format("%02d:%02d", hourOfDay, minute);
                editText.setText(timeText, TextView.BufferType.EDITABLE);
            }
        };
    }
}
