package cm.android.download.util;

import android.database.Cursor;

import java.io.Closeable;
import java.io.File;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

/**
 * IO读写Util类
 */
public class Util {

    private static final int BUF_SIZE = 8 * 1024;

    private Util() {
    }

    /**
     * Closes 'closeable', ignoring any checked exceptions. Does nothing if
     * 'closeable' is null.
     */
    public static void closeQuietly(Closeable closeable) {
        if (closeable == null) {
            return;
        }

        try {
            closeable.close();
        } catch (RuntimeException rethrown) {
            throw rethrown;
        } catch (Exception e) {
            LogUtil.getLogger().error(e.getMessage(), e);
        }
    }

    public static void closeQuietly(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    public static void checkState(boolean expression, String errorMessage) {
        if (!expression) {
            throw new IllegalStateException(String.valueOf(errorMessage));
        }
    }

    public static Certificate readCert(InputStream is) {
        CertificateFactory cf = null;
        try {
            cf = CertificateFactory.getInstance("X.509");
            return cf.generateCertificate(is);
        } catch (CertificateException e) {
            LogUtil.getLogger().error(e.getMessage(), e);
            return null;
        }
//        Log.i("Longer", "ca=" + ((X509Certificate) ca).getSubjectDN());
//        Log.i("Longer", "key=" + ((X509Certificate) ca).getPublicKey());
    }

    public static boolean checkDirectory(File file) {
        Util.checkState(file != null, "file = null");

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

//    public static void showFailedToast(Context context, long downloadId) {
//        if (context == null || downloadId < 0) {
//            return;
//        }
//
//        ContentResolver resolver = context.getApplicationContext().getContentResolver();
//        String selection = Downloads.Impl._ID + " = ? ";
//        String[] selectionArgs = new String[]{String.valueOf(downloadId)};
//
//        Cursor cursor = null;
//        try {
//            cursor = resolver.query(Downloads.Impl.ALL_DOWNLOADS_CONTENT_URI,
//                    null, selection, selectionArgs, null);
//            if (cursor != null && cursor.moveToFirst()) {
//                int columnIndex = cursor.getColumnIndexOrThrow(Downloads.Impl.COLUMN_STATUS);
//                int errorCode = cursor.getInt(columnIndex);
//                String errorMsg = cursor
//                        .getString(cursor.getColumnIndexOrThrow(Downloads.Impl.COLUMN_ERROR_MSG));
//                Toast.makeText(context,
//                        context.getString(R.string.download_failed, errorCode),
//                        Toast.LENGTH_SHORT).show();
//            }
//        } finally {
//            Util.closeQuietly(cursor);
//        }
//    }

}