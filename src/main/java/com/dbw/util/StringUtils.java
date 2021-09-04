package com.dbw.util;

import com.dbw.err.UnrecoverableException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;

public class StringUtils {

    public static String multiplyNTimes(int n, String str) {
        return String.join("", Collections.nCopies(n, str));
    }

    public static boolean isNumeric(String val) {
        return val.chars().allMatch(Character::isDigit);
    }

    public static String createShortHash(String val) {
        byte[] hash = new byte[]{};
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            hash = digest.digest(val.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            new UnrecoverableException("StringUtils", e.getMessage(), e).handle();
        }
        return bytesToHex(hash).substring(0, 16);
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

}
