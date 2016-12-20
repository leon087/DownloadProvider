package cm.android.download;

public final class DownloadConfig {
    public static final int MAX_CON_CURRENT_DOWNLOADS_ALLOWED = 5;

    public static long DOWNLOAD_MAX_BYTES_OVER_MOBILE = Long.MAX_VALUE;

    public static long DOWNLOAD_RECOMMENDED_MAX_BYTES_OVER_MOBILE = Long.MAX_VALUE;

//    public static final String getDownloadProviderId() {
//        try {
//            Class clazz = Class.forName("cm.android.download.Config");
//            Method method = ReflectUtil.getMethod(clazz, "getDownloadProviderId", new Class[]{});
//            String applicationId = ReflectUtil.doMethod(clazz, method, new Class[]{});
//            return applicationId;
//        } catch (ClassNotFoundException e) {
//            return "";
//        }
//    }
//
//    public static final String getAuthorities() {
//        String downloadProviderId = getDownloadProviderId();
//        if (Utils.isEmpty(downloadProviderId)) {
//            downloadProviderId = "DOWNLOAD_PROVIDER_ID";
//        }
//        return downloadProviderId + ".download.provider";
//    }

    public static String AUTHORITIES = "download.provider";

    public static final void authorities(String authorities) {
        AUTHORITIES = authorities;
    }
}
