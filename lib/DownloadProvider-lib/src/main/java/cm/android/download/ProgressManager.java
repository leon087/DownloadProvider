package cm.android.download;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import java.util.HashSet;
import java.util.Set;

import cm.android.download.provider.Downloads;

public class ProgressManager {

    private Context mContext;

    private DownloadManagerContentObserver mObserver;

    private HandlerThread mUpdateThread;

    private Handler mUpdateHandler;

    private DownloadCallback downloadCallback;

    private final Set<Long> mSet = new HashSet<>();

    private Handler.Callback mUpdateCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE:
                    update();
                    break;
                default:
                    break;
            }
            return false;
        }
    };

    private static final int MSG_UPDATE = 1;

    public ProgressManager() {
    }

    public void init(Context context, DownloadCallback callback) {
        mContext = context.getApplicationContext();
        this.downloadCallback = callback;

        mObserver = new DownloadManagerContentObserver();
        mContext.getContentResolver().registerContentObserver(Downloads.Impl.ALL_DOWNLOADS_CONTENT_URI, true, mObserver);

        mUpdateThread = new HandlerThread("MyDownloadManager");
        mUpdateThread.start();
        mUpdateHandler = new Handler(mUpdateThread.getLooper(), mUpdateCallback);
    }

    public void deInit() {
        mUpdateThread.quit();
        mContext.getContentResolver().unregisterContentObserver(mObserver);
        mObserver = null;
        mContext = null;
    }

    private class DownloadManagerContentObserver extends ContentObserver {

        public DownloadManagerContentObserver() {
            super(new Handler());
        }

        @Override
        public void onChange(final boolean selfChange) {
            enqueueUpdate();
        }
    }

    private void enqueueUpdate() {
        mUpdateHandler.removeMessages(MSG_UPDATE);
        mUpdateHandler.obtainMessage(MSG_UPDATE).sendToTarget();
    }

    private void update() {
        downloadCallback.onChange();
    }

    public interface DownloadCallback {

        void onChange();
    }
}
