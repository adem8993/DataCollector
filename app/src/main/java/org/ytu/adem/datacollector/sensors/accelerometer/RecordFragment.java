package org.ytu.adem.datacollector.sensors.accelerometer;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileObserver;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.ytu.adem.datacollector.DirectoryFileObserver;
import org.ytu.adem.datacollector.R;
import org.ytu.adem.datacollector.enums.PendingIntentRequestCode;
import org.ytu.adem.datacollector.model.FileItem;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Adem on 17.12.2017.
 */

public class RecordFragment extends Fragment {

    private FileObserver observer;
    private View v;

    public RecordFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_accelerometer_record, container, false);
        ListView recordList = (ListView) v.findViewById(R.id.recordList);
        File recordDirectory = getContext().getExternalFilesDir(null);
        File[] files = recordDirectory.listFiles();

        final ArrayList<FileItem> fileItems = new ArrayList();
        for (int i = files.length - 1; i >= 0; i--) {
            FileItem item = new FileItem(Uri.fromFile(files[i]), files[i].getName(), new Date(files[i].lastModified()));
            fileItems.add(item);
        }
        recordList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                viewTextFile(fileItems.get(i));
            }
        });
        observer = new DirectoryFileObserver(this, getContext().getExternalFilesDir(null).toString());
        observer.startWatching();
        ArrayAdapter<FileItem> listAdapter = new ArrayAdapter<FileItem>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, fileItems);
        recordList.setAdapter(listAdapter);
        return v;
    }

    private void viewTextFile(FileItem fileItem) {
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(fileItem.getPath(), "text/plain");
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getActivity().startActivity(Intent.createChooser(pdfIntent, "Uygulama Seç"));
    }

}
