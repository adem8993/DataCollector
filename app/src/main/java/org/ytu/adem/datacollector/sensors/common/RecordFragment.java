package org.ytu.adem.datacollector.sensors.common;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ListView;

import org.ytu.adem.datacollector.DirectoryFileObserver;
import org.ytu.adem.datacollector.R;
import org.ytu.adem.datacollector.model.FileItem;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private int defaultButtonColor;
    private ArrayList<FileItem> fileItems;

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
        File recordDirectory = getContext().getExternalFilesDir(this.configFileName + "/");
        File[] files = recordDirectory.listFiles();

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

        observer = new DirectoryFileObserver(this, getContext().getExternalFilesDir(null).toString());
        observer.startWatching();
        listAdapter = new ArrayAdapter<FileItem>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_multiple_choice, fileItems);
        recordList.setAdapter(listAdapter);
        return v;
    }

    private void initButtons() {
        viewButton = (AppCompatImageButton) v.findViewById(R.id.viewButton);
        viewButton.setOnClickListener(getViewButtonOnClickListener());
        deleteButton = (AppCompatImageButton) v.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(getDeleteButtonOnClickListener());
        sendButton = (AppCompatImageButton) v.findViewById(R.id.sendButton);
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

    private void openDeleteConfirmationDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Silme Onayı")
                .setMessage("Dosyaları silmek istediğinize emin misiniz?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        deleteFiles();
                        listAdapter.notifyDataSetChanged();
                        recordList.getCheckedItemPositions().clear();
                        setButtonStates();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void setButtonStates() {
        int checkedItemCount = recordList.getCheckedItemPositions().size();
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
        List<Integer> fileIndexes = new ArrayList<>();
        List<FileItem> toBeDeletedFiles = new ArrayList<>();
        int size = fileItems.size();
        for (int i = 0; i < size; i++) {
            if (recordList.getCheckedItemPositions().get(i)) {
                fileIndexes.add(i);
                toBeDeletedFiles.add(fileItems.get(i));
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

    }
}
