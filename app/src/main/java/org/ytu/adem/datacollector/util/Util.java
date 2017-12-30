package org.ytu.adem.datacollector.util;

import java.text.NumberFormat;

/**
 * Created by Adem on 10.12.2017.
 */

public class Util {
    public static String formatFloatValueByPrecision(float value, int precision) {
        NumberFormat formatter = NumberFormat.getInstance();
        formatter.setMaximumFractionDigits(precision);
        formatter.setMinimumFractionDigits(precision);
        return formatter.format(value);
    }

    public static String prepareFileHeader(String sensorType, int frequency, int precision) {
        return "/******************************************" + "\n" +
                "***  Sensör Tipi: " + sensorType + "\n" +
                "***  Frekans: " + frequency + "\n" +
                "***  Hassasiyet : " + precision + "\n" +
                "*******************************************/" + "\n";
    }

}
