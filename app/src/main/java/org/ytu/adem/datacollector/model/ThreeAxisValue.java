package org.ytu.adem.datacollector.model;

/**
 * Created by Adem on 9.09.2017.
 */

public class ThreeAxisValue {
    private String x;
    private String y;
    private String z;

    public ThreeAxisValue(String x, String y, String z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return String.format("%1$5s", x) + "; " + String.format("%1$5s", y) + "; " + String.format("%1$5s", z);
    }
}
