package cm.android.download;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

import java.io.File;

import cm.android.download.provider.Downloads;
import cm.java.util.Assertions;

public class DownloadUtil {

    public static boolean checkDirectory(File file) {
        Assertions.checkState(file != null, "file = null");

        if (file.exists()) {
            if (!file.isDirectory()) {
                return false;
            }
        } else {
            if (!file.mkdirs()) {
                return false;
            }
        }
        return true;
    }

    public static void showFailedToast(Context context, long downloadId) {
        if (context == null || downloadId < 0) {
            return;
        }

        ContentResolver resolver = context.getApplicationContext().getContentResolver();
        String selection = Downloads.Impl._ID + " = ? ";
        String[] selectionArgs = new String[]{String.valueOf(downloadId)};
        Cursor cursor = resolver.query(Downloads.Impl.ALL_DOWNLOADS_CONTENT_URI,
                null, selection, selectionArgs, null);
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(Downloads.Impl.COLUMN_STATUS);
            int errorCode = cursor.getInt(columnIndex);
            String errorMsg = cursor
                    .getString(cursor.getColumnIndexOrThrow(Downloads.Impl.COLUMN_ERROR_MSG));
            Toast.makeText(context,
                    context.getString(R.string.download_failed, errorCode),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
