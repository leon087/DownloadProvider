package cm.android.download;

import android.util.Log;

import java.io.File;

import cm.java.util.Assertions;

public class DownloadUtil {

    public static boolean checkDirectory(File file) {
        Assertions.checkState(file != null, "file = null");

        if (file.exists()) {
            if (!file.isDirectory()) {
                Log.e("ggg", file.getAbsolutePath() + " already exists and is not a directory");
                return false;
            }
        } else {
            if (!file.mkdirs()) {
                Log.e("ggg", "Unable to create directory: " + file.getAbsolutePath());
                return false;
            }
        }
        return true;
    }
}
