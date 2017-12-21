package org.ytu.adem.datacollector;

import android.os.FileObserver;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

/**
 * Created by Adem on 15.12.2017.
 */

public class DirectoryFileObserver extends FileObserver {
    private String aboslutePath = "path to your directory";
    private Fragment fragment;
    public DirectoryFileObserver(Fragment fragment, String path) {
        super(path,FileObserver.CREATE);
        aboslutePath = path;
        this.fragment = fragment;
    }
    @Override
    public void onEvent(int event, String path) {
        if (event == FileObserver.CREATE) {
            final FragmentTransaction ft = this.fragment.getFragmentManager().beginTransaction();
            ft.detach(this.fragment);
            ft.attach(this.fragment);
            ft.commit();
        }
    }
}
