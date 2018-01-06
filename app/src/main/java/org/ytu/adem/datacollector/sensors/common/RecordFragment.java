package org.ytu.adem.datacollector.sensors.common;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileObserver;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;

import org.ytu.adem.datacollector.DirectoryFileObserver;
import org.ytu.adem.datacollector.R;
import org.ytu.adem.datacollector.model.FileItem;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by Adem on 17.12.2017.
 */

public class RecordFragment extends Fragment {
    ArrayAdapter<FileItem> listAdapter;
    private FileObserver observer;
    private View v;
    private String configFileName;
    private AppCompatImageButton viewButton;
    private AppCompatImageButton deleteButton;
    private AppCompatImageButton sendButton;
    private ListView recordList;
    private ArrayList<FileItem> fileItems;
    private EditText filterDate;

    public RecordFragment() {

    }

    @SuppressLint("ValidFragment")
    public RecordFragment(String configFileName) {
        this.configFileName = configFileName;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_record, container, false);
        recordList = (ListView) v.findViewById(R.id.recordList);
        final File recordDirectory = getContext().getExternalFilesDir(this.configFileName + "/");
        File[] files = recordDirectory.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".txt");
            }
        });

        fileItems = new ArrayList();
        for (int i = files.length - 1; i >= 0; i--) {
            FileItem item = new FileItem(Uri.fromFile(files[i]), files[i].getName(), new Date(files[i].lastModified()));
            fileItems.add(item);
        }
        initButtons();
        recordList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                setButtonStates();
            }
        });
        filterDate = (EditText) v.findViewById(R.id.filterDate);
        initFilterDateListener();
        observer = new DirectoryFileObserver(this, recordDirectory.getPath()+"/");
        observer.startWatching(); //START OBSERVING
        listAdapter = new ArrayAdapter<FileItem>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_multiple_choice, fileItems);
        recordList.setAdapter(listAdapter);
        return v;
    }

    private void initFilterDateListener() {
        filterDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog().show();
            }
        });
    }

    private void initButtons() {
        viewButton = (AppCompatImageButton) v.findViewById(R.id.viewButton);
        viewButton.setOnClickListener(getViewButtonOnClickListener());
        deleteButton = (AppCompatImageButton) v.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(getDeleteButtonOnClickListener());
        sendButton = (AppCompatImageButton) v.findViewById(R.id.sendButton);
        sendButton.setOnClickListener(getSendButtonOnClickListener());
    }

    private View.OnClickListener getViewButtonOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedItemId = 0;
                for (int i = 0; i < recordList.getCheckedItemPositions().size(); i++) {
                    if (recordList.getCheckedItemPositions().get(i)) {
                        selectedItemId = i;
                    }
                }
                viewTextFile((FileItem) fileItems.get(selectedItemId));
            }
        };
    }

    private View.OnClickListener getDeleteButtonOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDeleteConfirmationDialog();
            }
        };
    }

    private View.OnClickListener getSendButtonOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMail();
            }
        };
    }

    private void openDeleteConfirmationDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Silme Onayı")
                .setMessage("Dosyaları silmek istediğinize emin misiniz?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        deleteFiles();
                        listAdapter.notifyDataSetChanged();
                        setButtonStates();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void setButtonStates() {
        int checkedItemCount = recordList.getCheckedItemCount();
        if (checkedItemCount == 0) {
            disableAllButtons();
        } else if (checkedItemCount == 1) {
            enableButton(viewButton, ContextCompat.getColor(getContext(), android.R.color.holo_blue_dark));
            enableButton(deleteButton, ContextCompat.getColor(getContext(), android.R.color.holo_red_dark));
            enableButton(sendButton, ContextCompat.getColor(getContext(), android.R.color.holo_green_dark));
        } else if (checkedItemCount > 1) {
            disableButton(viewButton);
        }
    }

    private void enableButton(AppCompatImageButton button, int color) {
        button.setClickable(true);
        button.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    private void disableButton(AppCompatImageButton button) {
        button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E0E0E0")));
        button.setClickable(false);
    }

    private void disableAllButtons() {
        int defaultColor = Color.parseColor("#E0E0E0");
        viewButton.setBackgroundTintList(ColorStateList.valueOf(defaultColor));
        deleteButton.setBackgroundTintList(ColorStateList.valueOf(defaultColor));
        sendButton.setBackgroundTintList(ColorStateList.valueOf(defaultColor));
        viewButton.setClickable(false);
        deleteButton.setClickable(false);
        sendButton.setClickable(false);
    }

    private void viewTextFile(FileItem fileItem) {
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(fileItem.getPath(), "text/plain");
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getActivity().startActivity(Intent.createChooser(pdfIntent, "Uygulama Seç"));
    }

    private void deleteFiles() {
        List<FileItem> toBeDeletedFiles = new ArrayList<>();
        int size = fileItems.size();
        for (int i = 0; i < size; i++) {
            if (recordList.getCheckedItemPositions().get(i)) {
                toBeDeletedFiles.add(fileItems.get(i));
                recordList.setItemChecked(i, false);
            }
        }

        for (int i = 0; i < toBeDeletedFiles.size(); i++) {
            fileItems.remove(0);
            File file = new File(toBeDeletedFiles.get(i).getPath().getPath());
            if (file.exists()) {
                if (!file.delete()) {
                    fileItems.add(toBeDeletedFiles.get(i));
                }
            }
        }
    }

    private void sendMail() {
        List<FileItem> selectedFiles = new ArrayList<>();
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("text/plain");
        SharedPreferences prefences = getContext().getSharedPreferences(configFileName, Context.MODE_PRIVATE);
        intent.setData(Uri.parse("mailto:" + prefences.getString(getString(R.string.shared_preferences_mail), null))); // only email apps should handle this
        int size = fileItems.size();
        for (int i = 0; i < size; i++) {
            if (recordList.getCheckedItemPositions().get(i)) {
                selectedFiles.add(fileItems.get(i));
            }
        }
        String path = selectedFiles.get(0).getPath().getPath();
        String zipFile = path.substring(0, path.lastIndexOf("/") + 1) + "test.zip";
        if (selectedFiles.size() > 1) {
            try {
                zip(selectedFiles, zipFile);
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(zipFile)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            intent.putExtra(Intent.EXTRA_STREAM, selectedFiles.get(0).getPath());
        }
        SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.records_mail_date_format));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Veri Toplayıcı(" + sdf.format(new Date()) + ")");
        intent.putExtra(Intent.EXTRA_TEXT, getMailText(selectedFiles));

        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private String getMailText(List<FileItem> fileItems) {
        String text = "Dosyalar: \n";
        int i = 1;
        for (FileItem fileItem : fileItems) {
            text += i + ". " + fileItem.getName() + "\n";
            i++;
        }
        return text;
    }

    private Dialog createDialog() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), getDateSetListener(), year, month, day);
    }


    private DatePickerDialog.OnDateSetListener getDateSetListener() {
        final ArrayList<FileItem> filteredFileItems = new ArrayList<>();
        return new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String selectedDate = String.format("%02d", day) + "." + String.format("%02d", month + 1) + "." + year;
                filterDate.setText(selectedDate);
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                for (FileItem fileItem : fileItems) {
                    if (sdf.format(fileItem.getLastModifyDate()).equals(selectedDate)) {
                        filteredFileItems.add(fileItem);
                    }
                }
                listAdapter = new ArrayAdapter<FileItem>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_multiple_choice, filteredFileItems);
                recordList.setAdapter(listAdapter);
                listAdapter.notifyDataSetChanged();

            }

        };
    }

    private void zip(List<FileItem> files, String zipFile) throws IOException {
        BufferedInputStream origin = null;
        ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));
        try {
            byte data[] = new byte[1024];

            for (FileItem fileItem : files) {
                String filePath = fileItem.getPath().getPath();
                FileInputStream fi = new FileInputStream(filePath);
                origin = new BufferedInputStream(fi, 1024);
                try {
                    ZipEntry entry = new ZipEntry(filePath.substring(filePath.lastIndexOf("/") + 1));
                    out.putNextEntry(entry);
                    int count;
                    while ((count = origin.read(data, 0, 1024)) != -1) {
                        out.write(data, 0, count);
                    }
                } finally {
                    origin.close();
                }
            }
        } finally {
            out.close();

        }
    }
}
