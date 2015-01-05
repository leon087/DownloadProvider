package cm.android.download;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import cm.android.download.providers.downloads.DownloadReceiver;
import cm.android.download.providers.downloads.DownloadService;
import cm.android.sdk.content.BaseBroadcastReceiver;
import cm.android.util.ObjectUtil;

public class DownloadManagerPro {

    private static final Logger logger = LoggerFactory.getLogger("download");

    private DownloadManager downloadManager;

    protected Context context;

    private final MyDownloadReceiver myDownloadReceiver = new MyDownloadReceiver();

    private final AtomicBoolean isInitAtomic = new AtomicBoolean(false);

    private DownloadReceiver downloadReceiver = new DownloadReceiver();

    public void init(Context context) {
        if (!isInitAtomic.compareAndSet(false, true)) {
            return;
        }

        this.context = context.getApplicationContext();
        downloadManager = new DownloadManager(context.getContentResolver(),
                context.getPackageName());
        downloadManager.setAccessAllDownloads(false);
        startDownloadService(this.context);
        myDownloadReceiver.register(this.context);
        downloadReceiver.register(this.context);
    }

    public void deInit() {
        if (!isInitAtomic.compareAndSet(true, false)) {
            return;
        }

        clearListener();
        downloadReceiver.unregister();
        stopDownloadService(context);
        myDownloadReceiver.unregister();
        context = null;
        downloadManager = null;
    }

    private void startDownloadService(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, DownloadService.class);
        context.startService(intent);
    }

    private void stopDownloadService(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, DownloadService.class);
        context.stopService(intent);
    }

    public DownloadManager.Request getDefaultRequest(String url) {
        Uri srcUri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(srcUri);
        // request.addRequestHeader("User-Agent", "Android"); // 添加一个Http请求报头，

        // 设置允许使用的网络类型
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE
                | DownloadManager.Request.NETWORK_WIFI);

        // 对于下载，考虑到流量费用，这里是否允许使用漫游。
        request.setAllowedOverRoaming(false);

        request.setShowRunningNotification(true); // 是否显示下载进度的提示
        // request.setTitle("Downloading"); //设置notification的标题
        // 设置一个描述信息，主要是最终显示的notification提示，可以随便写个自己区别
        // request.setDescription("Downloading");

        // 设置外部存储的公共目录，一般通过getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)方法获取。
        // request.setDestinationInExternalPublicDir(
        // Environment.DIRECTORY_DOWNLOADS, "/");
        // 设置目标存储在外部目录，一般位置可以用 getExternalFilesDir()方法获取。
        request.setDestinationInExternalFilesDir(context,
                Environment.DIRECTORY_DOWNLOADS, "/");

        // String filename = url.substring(url.lastIndexOf("/") + 1);
        // filename =path.getPath() + "/" + filename;
        // if (isFileExist(path, filename)) {
        // // StartapkinstallIntesnt();
        // return;
        // }
        // Uri dpath = Uri.fromFile(new File(path.getPath() + "/" + filename));
        // 设置下载后文件路径
        // request.setDestinationUri(dpath);

        // 设置mime类型，这里看服务器配置，一般国家化的都为utf-8编码。
        // request.setMimeType(String mimeType)

        request.setVisibleInDownloadsUi(true); // 设置下载管理类在处理过程中的界面是否显示
        return request;
    }

    public long enqueue(String url) {
        DownloadManager.Request request = getDefaultRequest(url);
        return enqueue(request);
    }

    public long enqueue(DownloadManager.Request request) {
        // request.setVisibleInDownloadsUi(true); //设置下载管理类在处理过程中的界面是否显示
        long enqueue = downloadManager.enqueue(request);
        return enqueue;
    }

    /**
     * 暂停
     */
    public void pause(long... ids) {
        downloadManager.pauseDownload(ids);
    }

    /**
     * 继续
     */
    public void resume(long... ids) {
        downloadManager.resumeDownload(ids);
    }

    /**
     * 删除
     */
    public int remove(long... ids) {
        return downloadManager.remove(ids);
    }

    /**
     * 重新下载，下载完成后才能调用（成功或失败）
     */
    public void restart(long... ids) {
        downloadManager.restartDownload(ids);
    }

    public int getStatus(long downloadId) {
        int status = 0;
        DownloadManager.Query query = new DownloadManager.Query()
                .setFilterById(downloadId);
        Cursor c = null;
        try {
            c = downloadManager.query(query);
            if (c != null && c.moveToFirst()) {
                status = c.getInt(c
                        .getColumnIndex(DownloadManager.COLUMN_STATUS));
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return status;
    }

    public DownloadManager.Query query(long id) {
        DownloadManager.Query myDownloadQuery = new DownloadManager.Query();
        myDownloadQuery.setFilterById(id);
        // Cursor cursor = downloadManager.query(myDownloadQuery);
        return myDownloadQuery;
    }

    public Cursor query(DownloadManager.Query query) {
        return downloadManager.query(query);
    }

    private class MyDownloadReceiver extends BaseBroadcastReceiver {

        MyDownloadReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            long reference = intent.getLongExtra(
                    DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            DownloadManager.Query myDownloadQuery = query(reference);
            Cursor cursor = query(myDownloadQuery);
            if (cursor.moveToFirst()) {
                int mStatusColumnId = cursor
                        .getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS);

                int status = cursor.getInt(mStatusColumnId);
                logger.info("status = " + status);
                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    listener.onDownloadSuccess(myDownloadQuery);
                } else if (status == DownloadManager.STATUS_FAILED) {
                    listener.onDownloadFailure(myDownloadQuery);
                } else {

                }
                // int fileNameIdx = myDownload
                // .getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
                // int fileUriIdx = cursor
                // .getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                // String fileUri = cursor.getString(fileUriIdx);
                // 判断下载成功/失败
            }
            cursor.close();
        }

        @Override
        public IntentFilter createIntentFilter() {
            IntentFilter filter = super.createIntentFilter();
            // 监听下载完成（包括成功或失败）
            filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            return filter;
        }
    }

    public static interface OnCompleteListener {

        void onDownloadSuccess(DownloadManager.Query downloadQuery);

        void onDownloadFailure(DownloadManager.Query downloadQuery);
    }

    private Set<OnCompleteListener> onCompleteListeners = ObjectUtil
            .newHashSet();

    private final OnCompleteListener listener = new OnCompleteListener() {
        @Override
        public void onDownloadSuccess(DownloadManager.Query downloadQuery) {
            synchronized (onCompleteListeners) {
                for (OnCompleteListener onCompleteListener : onCompleteListeners) {
                    onCompleteListener.onDownloadSuccess(downloadQuery);
                }
            }
        }

        @Override
        public void onDownloadFailure(DownloadManager.Query downloadQuery) {
            synchronized (onCompleteListeners) {
                for (OnCompleteListener onCompleteListener : onCompleteListeners) {
                    onCompleteListener.onDownloadFailure(downloadQuery);
                }
            }
        }
    };

    public void addOnCompleteListener(OnCompleteListener onCompleteListener) {
        synchronized (onCompleteListeners) {
            onCompleteListeners.add(onCompleteListener);
        }
    }

    public void removeOnCompleteListener(OnCompleteListener onCompleteListener) {
        synchronized (onCompleteListeners) {
            onCompleteListeners.remove(onCompleteListener);
        }
    }

    private void clearListener() {
        synchronized (onCompleteListeners) {
            onCompleteListeners.clear();
        }
    }

    public static boolean isDownloadSuccess(int status) {
        if (status == DownloadManager.STATUS_SUCCESSFUL) {
            return true;
        }
        return false;
    }

    public static boolean isDownloadCompleted(int status) {
        if (status == DownloadManager.STATUS_FAILED
                || isDownloadSuccess(status)) {
            return true;
        }
        return false;
    }
}
