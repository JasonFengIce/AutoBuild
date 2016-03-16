package core;

/**
 * Created by huaijie on 10/22/15.
 */
public class Logger {




    public static void logError(String tag, String msg) {
        System.err.println(tag + " =======> " + msg);

    }

    public static void logInfo(String tag, String msg) {
        System.out.println(tag + " =======> " + msg);
    }
}
