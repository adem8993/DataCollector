package org.ytu.adem.datacollector.model;

import android.net.Uri;

import java.util.Date;

/**
 * Created by Adem on 14.12.2017.
 */

public class FileItem {
    private Uri path;
    private String name;
    private Date lastModifyDate;

    public FileItem(Uri path, String name, Date lastModifyDate) {
        this.path = path;
        this.name = name;
        this.lastModifyDate = lastModifyDate;
    }

    public Uri getPath() {
        return path;
    }

    public void setPath(Uri path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getLastModifyDate() {
        return lastModifyDate;
    }

    public void setLastModifyDate(Date lastModifyDate) {
        this.lastModifyDate = lastModifyDate;
    }

    @Override
    public String toString() {
        return name;
    }
}
