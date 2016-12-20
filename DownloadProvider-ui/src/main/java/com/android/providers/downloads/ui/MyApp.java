package com.android.providers.downloads.ui;

import android.app.Application;
import android.content.Context;

import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import cm.android.download.DownloadConfig;
import cm.android.download.DownloadHttps;
import cm.android.download.ui.BuildConfig;
import cm.android.download.util.Util;

public class MyApp extends Application {
    private static Context context;
    public static final String cerName = "12306.cer";

    @Override
    protected void attachBaseContext(Context base) {
        DownloadConfig.authorities(BuildConfig.APPLICATION_ID + "." + DownloadConfig.AUTHORITIES);

        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.context = this;
    }

    public static Context getContext() {
        return context;
    }


    public static void setDownload() {
        try {
            TrustManagerFactory tmf = createTrustManagerFactory(getContext(), cerName);
            DownloadHttps.setDefaultSSLSocketFactory(tmf, null);
            DownloadHttps.setDefaultHostnameVerifier(hostnameVerifier);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final HostnameVerifier hostnameVerifier = new HostnameVerifier() {

        @Override
        public boolean verify(String hostname, SSLSession session) {
            HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();

            return hv.verify(hostname, session);
        }
    };

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
