package org.ytu.adem.datacollector.util;

import java.text.NumberFormat;

/**
 * Created by Adem on 10.12.2017.
 */

public class Util {
    public static String formatFloatValueByPrecision(float value, int precision) {
        NumberFormat formatter = NumberFormat.getInstance();
        formatter.setMaximumFractionDigits(precision);
        return formatter.format(value);
    }
}
