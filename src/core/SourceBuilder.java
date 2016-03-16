package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by huaijie on 10/24/15.
 */
public class SourceBuilder {
    private static final String TAG = "SourceBuilder";

    private static SourceBuilder ourInstance = new SourceBuilder();

    public static SourceBuilder getInstance() {
        return ourInstance;
    }

    private SourceBuilder() {
    }


    public boolean cleanSource() {
        Logger.logInfo(TAG, "start clean source...");
        try {
            Runtime runtime = Runtime.getRuntime();
            System.out.println(new File(Constants.WORK_DIRECTORY).getAbsolutePath() + "/build.gradle");
            Process process1 = runtime.exec("gradle -q -b " + new File(Constants.WORK_DIRECTORY).getAbsolutePath() + "/build.gradle clean");


            InputStreamReader ir1 = new InputStreamReader(process1.getInputStream());
            BufferedReader input1 = new BufferedReader(ir1);
            String line1;

            while ((line1 = input1.readLine()) != null) {
                Logger.logInfo(TAG, line1);
                if (line1.contains("BUILD FAILED")) {
                    return false;
                }
            }
            Logger.logInfo(TAG, "clean source success!!!");
        } catch (IOException e) {
            Logger.logError(TAG, e.getMessage());
            return false;
        }
        return true;
    }

    public boolean buildSource() {
        cleanSource();

        Logger.logInfo(TAG, "start build source...");
        try {
            Runtime runtime = Runtime.getRuntime();

            Process process1 = runtime.exec("gradle -q -b " + new File(Constants.WORK_DIRECTORY).getAbsolutePath() + "/build.gradle build");
            InputStreamReader ir1 = new InputStreamReader(process1.getInputStream());
            BufferedReader input1 = new BufferedReader(ir1);
            String line1;

            while ((line1 = input1.readLine()) != null) {
                Logger.logInfo(TAG, line1);
                if (line1.contains("BUILD FAILED")) {
                    return false;
                }
            }
            Logger.logInfo(TAG, "build source success!!!");
        } catch (IOException e) {
            Logger.logError(TAG, e.getMessage());
            return false;
        }
        return true;
    }
}
