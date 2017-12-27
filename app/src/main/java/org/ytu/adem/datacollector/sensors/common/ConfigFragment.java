package org.ytu.adem.datacollector.sensors.common;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import org.ytu.adem.datacollector.R;
import org.ytu.adem.datacollector.util.InputFilterMinMax;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.MODE_PRIVATE;


/**
 * Created by Adem on 20.08.2017.
 */

public class ConfigFragment extends Fragment {
    EditText frequency;
    EditText fileName;
    EditText mail;
    EditText precision;
    Spinner dateFormat;
    SharedPreferences preferences;
    private int sensorType;
    private String configFileName;

    public ConfigFragment() {

    }

    @SuppressLint("ValidFragment")
    public ConfigFragment(String configFileName) {
        this.configFileName = configFileName;
    }

    private void initFrequencyEditTextListener() {
        frequency.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt("frequency", Integer.valueOf(s.toString()));
                    editor.commit();
                }
            }
        });
    }

    private void initFileNameEditTextListener() {
        fileName.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("fileName", s.toString());
                    editor.commit();
                }
            }
        });
    }

    private void initMailEditTextListener() {
        mail.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0 && checkCorrectEmail(s.toString())) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(getResources().getString(R.string.shared_preferences_mail), s.toString());
                    editor.commit();
                }
            }
        });
    }

    private void initPrecisionEditTextListener() {
        precision.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt("precision", Integer.valueOf(s.toString()));
                    editor.commit();
                }
            }
        });
    }

    private void initDateFormatSpinnerListener() {
        dateFormat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedDateFormat = (String) adapterView.getItemAtPosition(i);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("dateFormat", selectedDateFormat);
                editor.commit();
                dateFormat.setSelection(i);
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_config, container, false);
    }

    private void initFrequency() {
        frequency = (EditText) getActivity().findViewById(R.id.frequency);
        frequency.setFilters(new InputFilter[]{new InputFilterMinMax(0, 10)});
        frequency.setText(String.valueOf(preferences.getInt("frequency", 1)));
        initFrequencyEditTextListener();
    }

    private void initFileName() {
        fileName = (EditText) getActivity().findViewById(R.id.fileName);
        fileName.setText(preferences.getString("fileName", "Tanımsız"));
        initFileNameEditTextListener();
    }

    private void initMail() {
        mail = (EditText) getActivity().findViewById(R.id.mail);
        mail.setText(preferences.getString(getResources().getString(R.string.shared_preferences_mail), "test@mail.com"));
        initMailEditTextListener();
    }

    private void initPrecision() {
        precision = (EditText) getActivity().findViewById(R.id.precision);
        precision.setFilters(new InputFilter[]{new InputFilterMinMax(0, 5)});
        precision.setText(String.valueOf(preferences.getInt("precision", 2)));
        initPrecisionEditTextListener();
    }

    private void initDateFormat() {
        dateFormat = (Spinner) getActivity().findViewById(R.id.dateFormat);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.date_formats, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateFormat.setAdapter(adapter);
        dateFormat.setSelection(((ArrayAdapter<String>) dateFormat.getAdapter()).getPosition(preferences.getString("dateFormat", getString(R.string.default_date_format))));
        initDateFormatSpinnerListener();
    }


    private boolean checkCorrectEmail(String mailAddress) {
        TextInputLayout input = (TextInputLayout) getActivity().findViewById(R.id.mailLabel);
        boolean isValidated = false;
        if (!validateEmail(mailAddress.trim())) {
            input.setError("Uygun formatta değil!(foo@bar.co)");
        } else {
            isValidated = true;
            input.setError(null);
        }
        return isValidated;
    }

    private boolean validateEmail(String email) {
        String emailPattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


    @Override
    public void onStart() {
        super.onStart();
        preferences = getContext().getSharedPreferences(this.configFileName, MODE_PRIVATE);
        initFrequency();
        initFileName();
        initMail();
        initPrecision();
        initDateFormat();
    }
}
