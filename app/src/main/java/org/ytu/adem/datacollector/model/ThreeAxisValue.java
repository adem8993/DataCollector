package org.ytu.adem.datacollector.model;

/**
 * Created by Adem on 9.09.2017.
 */

public class ThreeAxisValue {
    private String x;
    private String y;
    private String z;
    private String date;

    public ThreeAxisValue(String date, String x, String y, String z) {
        this.date = date;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return date + "; " + x + "; " + y + "; " + z;
    }
}
