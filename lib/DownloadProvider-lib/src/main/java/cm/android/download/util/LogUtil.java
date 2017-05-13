package cm.android.download.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUtil {
    private static final Logger logger = LoggerFactory.getLogger("download");

    public static Logger getLogger() {
        return logger;
    }
}
