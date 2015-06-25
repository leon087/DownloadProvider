package cm.android.download;

import android.content.Context;
import android.content.Intent;

import cm.android.download.providers.downloads.DownloadService;

public class DownloadServer {

    public static void start(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, DownloadService.class);
        context.startService(intent);
    }

    public static void stop(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, DownloadService.class);
        context.stopService(intent);
    }
}
