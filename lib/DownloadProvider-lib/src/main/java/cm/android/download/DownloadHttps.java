package cm.android.download;

import android.content.Context;

import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.util.Arrays;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import cm.android.download.util.Util;

public class DownloadHttps {
//    public static final void wrap(URLConnection urlConnection) {
//        if (urlConnection instanceof HttpsURLConnection) {
////            SSLSocketFactory sslSocketFactory = HttpsURLConnection.getDefaultSSLSocketFactory();
//////            SSLSocketFactory sslSocketFactory = getSSLSocketFactory();
////            if (sslSocketFactory != null) {
////                ((HttpsURLConnection) urlConnection).setSSLSocketFactory(sslSocketFactory);
////            }
////
//////            HostnameVerifier hostnameVerifier = getHostnameVerifier();
////            HostnameVerifier hostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier();
////            if (hostnameVerifier != null) {
////                ((HttpsURLConnection) urlConnection).setHostnameVerifier(hostnameVerifier);
////            }
////            HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
//        }
//    }

    public static void setDefaultSSLSocketFactory(TrustManagerFactory tmf, KeyManagerFactory kmf)
            throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = getSSLContext(tmf, kmf);
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
    }

    public static void setDefaultHostnameVerifier(HostnameVerifier hostnameVerifier)
            throws NoSuchAlgorithmException, KeyManagementException {
        HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
    }

    public static SSLContext getSSLContext(TrustManagerFactory tmf, KeyManagerFactory kmf) throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] trustManagers = tmf.getTrustManagers();

        if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
            throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
        }

        SSLContext sslContext = SSLContext.getInstance("TLS");
//        SSLContext context = SSLContext.getInstance("TLSv1","AndroidOpenSSL");
        KeyManager[] km = null;
        if (kmf != null) {
            km = kmf.getKeyManagers();
        }
        sslContext.init(km, trustManagers, new SecureRandom());
        return sslContext;
    }

    public static TrustManagerFactory createTrustManagerFactory(Context context, String fileName)
            throws Exception {
        InputStream trustIs = null;
        try {
            trustIs = context.getResources().getAssets().open(fileName);
            Certificate certificate = Util.readCert(trustIs);
            if (certificate == null) {
                return null;
            }
            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(null, null);
            keystore.setCertificateEntry("ca", certificate);

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keystore);
            return tmf;
        } finally {
            Util.closeQuietly(trustIs);
        }
    }
}
