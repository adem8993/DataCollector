package org.ytu.adem.datacollector.model;

/**
 * Created by adem on 21.12.2017.
 */

public class RecordLength {
    private int hour;
    private int minute;

    public RecordLength() {
    }

    public RecordLength(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    @Override
    public String toString() {
        return "RecordLength{" +
                "hour=" + hour +
                ", minute=" + minute +
                '}';
    }
}
