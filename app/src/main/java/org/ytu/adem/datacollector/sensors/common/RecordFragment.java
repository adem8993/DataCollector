package org.ytu.adem.datacollector.sensors.common;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.FileObserver;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
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

/**
 * Created by Adem on 17.12.2017.
 */

public class RecordFragment extends Fragment {

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
        ArrayAdapter<FileItem> listAdapter = new ArrayAdapter<FileItem>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_multiple_choice, fileItems);
        recordList.setAdapter(listAdapter);
        return v;
    }

    private void initButtons() {
        viewButton = (AppCompatImageButton) v.findViewById(R.id.viewButton);
        viewButton.setOnClickListener(new View.OnClickListener() {
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
        });
        deleteButton = (AppCompatImageButton) v.findViewById(R.id.deleteButton);
        sendButton = (AppCompatImageButton) v.findViewById(R.id.sendButton);

    }

    private void setButtonStates() {
        int checkedItemCount = recordList.getCheckedItemCount();
        if (checkedItemCount == 0) {
            disableAllButtons();
        } else if (checkedItemCount == 1) {
            enableButton(viewButton, getResources().getColor(android.R.color.holo_blue_dark));
            enableButton(deleteButton, getResources().getColor(android.R.color.holo_red_dark));
            enableButton(sendButton, getResources().getColor(android.R.color.holo_green_dark));
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

    private void sendMail() {

    }
}
