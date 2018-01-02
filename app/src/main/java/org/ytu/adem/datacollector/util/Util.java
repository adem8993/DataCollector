package org.ytu.adem.datacollector.util;

import android.hardware.Sensor;

import java.text.NumberFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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

    public static String prepareMultipleFileHeader(LinkedList<Integer> sensorType, List<Integer> frequencies, int precision) {
        if (sensorType.size() != frequencies.size()) return "/***** HATA!  *****/";
        Iterator sensorIterator = sensorType.iterator();
        int[] typeLength = new int[10];
        int typeCount = 0;
        String typeName = "Bilinmeyen";
        String sensorTypeLine = "***  Sensor Tipleri: ";
        if (sensorIterator.hasNext())
            typeName = findSensorNameByType((Integer) sensorIterator.next());
        typeLength[typeCount] = typeName.length();
        typeCount++;
        sensorTypeLine += typeName;
        while (sensorIterator.hasNext()) {
            typeName = findSensorNameByType((Integer) sensorIterator.next());
            typeLength[typeCount] = typeName.length();
            typeCount++;
            sensorTypeLine += " || " + typeName;
        }
        String frequenceLine = "***  Frekanslar:     " + formatFrequencyByTypeLength(frequencies.get(0), typeLength[0]);
        for (int i = 1; i < frequencies.size(); i++) {
            frequenceLine += " || " + formatFrequencyByTypeLength(frequencies.get(i), typeLength[i]);
        }
        int headerLength = getHeaderLength(typeLength, frequencies.size());
        return "/" + fillWithStars(headerLength) + "\n" +
                sensorTypeLine + "  ***" + "\n" +
                frequenceLine + "  ***" + "\n" +
                "***  Hassasiyet: " + precision + String.format("%" + (headerLength - 17) + "s", "***") + "\n" +
                fillWithStars(headerLength) + "/" + "\n";
    }

    private static String fillWithStars(int length) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < length) {
            sb.append("*");
            i++;
        }
        return sb.toString();
    }

    private static int getHeaderLength(int[] typeLength, int numberOfSensors) {
        int headerLength;
        final int prefixLength = 20;
        headerLength = prefixLength;
        for (int i = 0; i < numberOfSensors; i++) {
            headerLength += typeLength[i] + 4;
        }
        headerLength++;
        return headerLength;
    }

    private static String formatFrequencyByTypeLength(int frequency, int typeLength) {
        return String.format("%" + typeLength + "s", String.valueOf(frequency));
    }


    private static String findSensorNameByType(int sensorType) {
        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                return "İvmeölçer";
            case Sensor.TYPE_LINEAR_ACCELERATION:
                return "Lineer İvme";
            case Sensor.TYPE_GRAVITY:
                return "Yer Çekimi";
            case Sensor.TYPE_GYROSCOPE:
                return "Jiroskop";
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                return "Nem";
            case Sensor.TYPE_LIGHT:
                return "Işık";
            case Sensor.TYPE_MAGNETIC_FIELD:
                return "Manyetik Alan";
            case Sensor.TYPE_PRESSURE:
                return "Basınç";
            case Sensor.TYPE_PROXIMITY:
                return "Yakınlık";
            case Sensor.TYPE_ROTATION_VECTOR:
                return "Dönüş Vektörü";
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                return "Sıcaklık";
            case Sensor.TYPE_ALL:
                return "Birden Çok";
            default:
        }
        return "Bilinmeyen";
    }
}
