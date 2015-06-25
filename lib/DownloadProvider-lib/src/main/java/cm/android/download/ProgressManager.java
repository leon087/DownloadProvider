package cm.android.download;


import com.google.common.collect.Sets;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import java.util.Set;

import cm.android.download.provider.Downloads;

public class ProgressManager {

    private Context mContext;

    private DownloadManager mDownloadManager;

    private DownloadManagerContentObserver mObserver;

    private HandlerThread mUpdateThread;

    private Handler mUpdateHandler;

    private DownloadCallback downloadCallback;

    private final Set<Long> mSet = Sets.newHashSet();

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

    public ProgressManager(Context context, DownloadCallback callback) {
        mContext = context;
        mDownloadManager = new DownloadManager(context.getContentResolver(),
                context.getPackageName());
        this.downloadCallback = callback;
    }

    public void init() {
        mObserver = new DownloadManagerContentObserver();
        mContext.getContentResolver()
                .registerContentObserver(Downloads.Impl.ALL_DOWNLOADS_CONTENT_URI, true, mObserver);

        mUpdateThread = new HandlerThread("MyDownloadManager");
        mUpdateThread.start();
        mUpdateHandler = new Handler(mUpdateThread.getLooper(), mUpdateCallback);
    }

    public void deInit() {
        mContext.getContentResolver().unregisterContentObserver(mObserver);
        mObserver = null;
        mUpdateThread.quit();
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
        /*
        mDownloads.clear();
        String selection = Downloads.Impl.COLUMN_STATUS + " != ? ";
        String[] selectionArgs = new String[]{String.valueOf(Downloads.Impl.STATUS_SUCCESS)};
        final ContentResolver resolver = mContext.getContentResolver();
        final Cursor cursor = resolver.query(Downloads.Impl.ALL_DOWNLOADS_CONTENT_URI,
                null, selection, selectionArgs, null);
        try {
            DownloadInfo.Reader reader = new DownloadInfo.Reader(resolver, cursor);
            final int idColumn = cursor.getColumnIndexOrThrow(Downloads.Impl._ID);
            while (cursor.moveToNext()) {
                long id = cursor.getLong(idColumn);
                DownloadInfo info = mDownloads.get(id);
                if (info != null) {
                    updateDownload(reader, info);
                } else {
                    insertDownloadLocked(reader);
                }
            }
            downloadCallback.reportProgress(mDownloads.values());
        } finally {
            cursor.close();
        }
        */
    }

    public interface DownloadCallback {

        void onChange();
    }
}
