package com.bcsim.core;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class NodeUtils {
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] calculateHash(byte[] source) {
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA1");

            sha1.update(source);

            return sha1.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean compareHashes(byte[] h1, byte[] h2) {
        if (h1.length != h2.length) {
            return false;
        }

        for (int i = 0; i < h1.length; i++) {
            if (h1[i] != h2[i]) {
                return false;
            }
        }

        return true;
    }
}
