package org.ytu.adem.datacollector.util;


import android.content.Context;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created by Adem on 9.09.2017.
 */

public class IOUtil {

    /**
     * @param textList
     * @param writeToExternal
     */
    public static void writeToFile(Context context, List<String> textList, boolean writeToExternal) {
        File path;
        if (writeToExternal) {
            path = context.getExternalFilesDir(null);
        } else {
            path = context.getFilesDir();
        }
        try {
            FileWriter writer = new FileWriter(new File(path, "a.txt"));
            for(String line: textList) {
                writer.write(line);
            }
            writer.close();
        } catch (IOException e) {

        }
    }
}
