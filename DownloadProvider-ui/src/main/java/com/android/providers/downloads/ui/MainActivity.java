package com.android.providers.downloads.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import cm.android.download.DownloadManager;
import cm.android.download.DownloadManagerPro;
import cm.android.download.ui.R;


public class MainActivity extends Activity implements View.OnClickListener {
    private DownloadManagerPro downloadManager = new DownloadManagerPro();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        downloadManager.init(this);
        Button btnStart = (Button) findViewById(R.id.start);
        Button btnSttop = (Button) findViewById(R.id.stop);
        Button btnLook = (Button) findViewById(R.id.look);
        btnStart.setOnClickListener(this);
        btnSttop.setOnClickListener(this);
        btnLook.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        downloadManager.deInit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    private static final String TAG = "ggg";
    // an ID for the download, unique across the system. This ID is used to make
    // future calls related to this download.
    long mDownloadId;


    @Override
    protected void onPause() {
        stopQuery();
        this.unregisterReceiver(receiver);
        this.unregisterReceiver(receiver2);

        super.onPause();
    }

    @Override
    protected void onResume() {
        IntentFilter filter = new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        this.registerReceiver(receiver, filter);

        IntentFilter filter22 = new IntentFilter(
                DownloadManager.ACTION_NOTIFICATION_CLICKED);
        this.registerReceiver(receiver2, filter22);

        startQuery(mDownloadId);
        super.onResume();
    }

    public void lookDownload() {
        startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.start) {
            DownloadManager.Request request = new DownloadManager.Request(
                    Uri.parse("http://dl.coolapk.com/dl5.php?url=%2Fapk%2Fcom.netease.vopen&apkname=com.netease.vopen&version=3.1.1&filename=%E7%BD%91%E6%98%93%E5%85%AC%E5%BC%80%E8%AF%BE&file=%2Fapk_file%2F2014%2F1210%2Fcom.netease.vopen-3.1.1.apk&extra=0&h=39d84b94ngdaxh"));

            request.setAllowedNetworkTypes(
                    DownloadManager.Request.NETWORK_MOBILE
                            | DownloadManager.Request.NETWORK_WIFI)
                    .setAllowedOverRoaming(false) // 缺省是true
                    .setTitle("更新") // 用于信息查看
                    .setDescription("下载apk") // 用于信息查看
                    .setDestinationInExternalPublicDir(
                            Environment.DIRECTORY_DOWNLOADS, "test.zip");
            request.setVisibleInDownloadsUi(true);
            mDownloadId = downloadManager.enqueue(request); // 加入下载队列

            startQuery(mDownloadId);
        }
        if (id == R.id.stop) {
            stopQuery();
            removeDownload(mDownloadId);
        }
        if (id == R.id.look) {
            lookDownload();
        }
    }

    private int removeDownload(long downloadId) {
        return downloadManager.remove(downloadId);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    int step = 1000;
    QueryRunnable runnable = new QueryRunnable();

    class QueryRunnable implements Runnable {
        public long DownID;

        @Override
        public void run() {
            queryState(DownID);
            handler.postDelayed(runnable, step);
        }
    }

    private void startQuery(long downloadId) {
        if (downloadId != 0) {
            runnable.DownID = downloadId;
            handler.postDelayed(runnable, step);
        }
    }

    private void stopQuery() {
        handler.removeCallbacks(runnable);
    }

    private void queryState(long downID) {
        // 关键：通过ID向下载管理查询下载情况，返回一个cursor
        Cursor c = downloadManager.query(new DownloadManager.Query()
                .setFilterById(downID));
        if (c == null) {
            Toast.makeText(this, "Download not found!", Toast.LENGTH_LONG)
                    .show();
        } else { // 以下是从游标中进行信息提取
            if (!c.moveToFirst()) {
                c.close();
                return;
            }
            Log.d(TAG, "ggg Column_id : " + c.getLong(c.getColumnIndex(DownloadManager.COLUMN_ID)));
            Log.d(TAG, "ggg Column_bytes_downloaded so far : " + c.getLong(c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)));
            Log.d(TAG, "ggg Column last modified timestamp : " + c.getLong(c.getColumnIndex(DownloadManager.COLUMN_LAST_MODIFIED_TIMESTAMP)));
            Log.d(TAG, "ggg Column local uri : " + c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)));
            Log.d(TAG, "ggg Column statue : " + c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS)));
            Log.d(TAG, "ggg Column reason : " + c.getInt(c.getColumnIndex(DownloadManager.COLUMN_REASON)));

            int st = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
//            Toast.makeText(this, statusMessage(st), Toast.LENGTH_LONG).show();
            //Log.i(TAG, statusMessage(st));

            c.close();
        }
    }

    private String statusMessage(int st) {
        switch (st) {
            case DownloadManager.STATUS_FAILED:
                return "Download failed";
            case DownloadManager.STATUS_PAUSED:
                return "Download paused";
            case DownloadManager.STATUS_PENDING:
                return "Download pending";
            case DownloadManager.STATUS_RUNNING:
                return "Download in progress!";
            case DownloadManager.STATUS_SUCCESSFUL:
                return "Download finished";
            default:
                return "Unknown Information";
        }
    }

    // 监听下载结束，启用BroadcastReceiver
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {

                long downloadId = intent.getLongExtra(
                        DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                // 查询
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(downloadId);
                Cursor c = downloadManager.query(query);
                if (c.moveToFirst()) {
                    int columnIndex = c
                            .getColumnIndex(DownloadManager.COLUMN_STATUS);
                    if (DownloadManager.STATUS_SUCCESSFUL == c
                            .getInt(columnIndex)) {

                        String uriString = c
                                .getString(c
                                        .getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));

                        //removeDownload(downloadId);
                        Toast.makeText(MainActivity.this,
                                "get file complete: " + uriString, Toast.LENGTH_LONG).show();
                        // Uri.parse(uriString);
                    }
                }// endif

            }// endif

        }// onReceive
    };// end class receiver

    // 监听下载结束，启用BroadcastReceiver
    BroadcastReceiver receiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long downloadId = intent.getLongExtra(
                    DownloadManager.EXTRA_DOWNLOAD_ID, 0);

            String action = intent.getAction();
            if (DownloadManager.ACTION_NOTIFICATION_CLICKED.equals(action)) {
                lookDownload();
            }
        }
    };//
}
