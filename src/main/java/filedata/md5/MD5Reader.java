package filedata.md5;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Reader {

    private static final int BUFFER_SIZE = 8192;

    public static String getMD5(File file) throws IOException, SecurityException {

        validateFile(file);

        if (file == null || !file.exists() || !file.isFile()) {
            return null;
        }

        if (file.length() == 0) {
            return "d41d8cd98f00b204e9800998ecf8427e";
        }

        try (InputStream is = new FileInputStream(file)) {
            return calculateMD5(is);
        }
    }

    private static void validateFile(File file) {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null");
        }
        if (!file.exists()) {
            throw new IllegalArgumentException("File does not exist: " + file);
        }
        if (!file.isFile()) {
            throw new IllegalArgumentException("Not a regular file: " + file);
        }
    }

    private static String calculateMD5(InputStream is) throws IOException, SecurityException {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;

            while ((bytesRead = is.read(buffer)) != -1) {
                md.update(buffer, 0, bytesRead);
            }

            return bytesToHex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new SecurityException("MD5 algorithm not available", e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
