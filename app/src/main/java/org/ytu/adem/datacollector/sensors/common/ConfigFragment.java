package org.ytu.adem.datacollector.sensors.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageButton;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import org.ytu.adem.datacollector.R;
import org.ytu.adem.datacollector.db.DatabaseHandler;
import org.ytu.adem.datacollector.util.InputFilterMinMax;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.MODE_PRIVATE;


/**
 * Created by Adem on 20.08.2017.
 */

public class ConfigFragment extends Fragment {
    private EditText frequency;
    private Spinner fileName;
    private EditText mail;
    private EditText precision;
    private Spinner dateFormat;
    private SharedPreferences preferences;
    private String configFileName;
    private boolean isMultiple;
    private SQLiteDatabase db;

    public ConfigFragment() {

    }

    @SuppressLint("ValidFragment")
    public ConfigFragment(String configFileName, boolean isMultiple) {
        this.configFileName = configFileName;
        this.isMultiple = isMultiple;
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

    private void initFileNameSpinnerListener() {
        fileName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedFileName = (String) adapterView.getItemAtPosition(i);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("fileName", selectedFileName);
                editor.commit();
                fileName.setSelection(i);
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
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
                    editor.putInt(getString(R.string.shared_preferences_precision), Integer.valueOf(s.toString()));
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
        if (this.isMultiple) {
            frequency.setVisibility(View.GONE);
        } else {
            frequency.setFilters(new InputFilter[]{new InputFilterMinMax(0, 100)});
            frequency.setText(String.valueOf(preferences.getInt("frequency", 1)));
            initFrequencyEditTextListener();
        }
    }

    private void initFileName() {
        fileName = (Spinner) getActivity().findViewById(R.id.fileName);
        loadActivityData();
        initFileNameSpinnerListener();
    }

    private void initAddActivityButton() {
        AppCompatImageButton addButton = (AppCompatImageButton) getActivity().findViewById(R.id.addActivity);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddActivityDialog();
            }
        });
    }

    private void openAddActivityDialog() {
        final EditText activityName = new EditText(getContext());
        activityName.setSingleLine();
        FrameLayout container = new FrameLayout(getActivity());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
        activityName.setLayoutParams(params);
        container.addView(activityName);
        new AlertDialog.Builder(getContext())
                .setTitle("Aktivite Adı")
                .setView(container)
                .setPositiveButton("Ekle", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        addNewActivity(activityName);
                    }
                })
                .setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
    }

    private void addNewActivity(EditText inputLabel) {
        String label = inputLabel.getText().toString();

        if (label.trim().length() > 0) {
            // database handler
            DatabaseHandler db = new DatabaseHandler(getActivity().getApplicationContext());

            // inserting new label into database
            db.insertLabel(label);

            // making input filed text to blank
            inputLabel.setText("");

            // Hiding the keyboard
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(inputLabel.getWindowToken(), 0);

            // loading spinner with newly added data
            loadActivityData();
        } else {
            Toast.makeText(getContext(), "Aktivite adı giriniz",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void loadActivityData() {
        // database handler
        // Spinner Drop down elements
        DatabaseHandler dbHandler = new DatabaseHandler(getActivity().getApplicationContext());
        List<String> activities = dbHandler.getAllActivities();

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, activities);

        // Drop down layout style - list view with radio button
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        fileName.setAdapter(dataAdapter);
        String selectedValue = preferences.getString(getString(R.string.shared_preferences_fileName), "Genel");
        int pos = dataAdapter.getPosition(selectedValue);
        fileName.setSelection(pos);
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
                R.array.date_formats, android.R.layout.preference_category);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
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
        initMail();
        initPrecision();
        initDateFormat();
        initAddActivityButton();
        initFileName();

    }
}
