package com.dbw.util;

import java.util.Collections;

public class StringUtils {

    public static String multiplyNTimes(int n, String str) {
        return String.join("", Collections.nCopies(n, str));
    }

    public static boolean isNumeric(String val) {
        return val.chars().allMatch(Character::isDigit);
    }

}
