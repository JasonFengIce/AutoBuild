package utils;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * Created by huaijie on 10/20/15.
 */
public class Utils {

    public static String getMd5ByFile(File file) {
        String value;
        FileInputStream in;
        try {
            in = new FileInputStream(file);
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[1024 * 1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                messageDigest.update(buffer, 0, length);
            }
            BigInteger bi = new BigInteger(1, messageDigest.digest());
            value = bi.toString(16);
            in.close();
        } catch (Exception e) {
            System.out.println("getMd5ByFile: " + e.getMessage());
            return "";
        }

        int offset = 32 - value.length();
        if (offset > 0) {
            String data = new String();
            for (int i = 0; i < offset; i++) {
                data = data + "0";
            }
            value = data + value;
        }
        return value;
    }
}
