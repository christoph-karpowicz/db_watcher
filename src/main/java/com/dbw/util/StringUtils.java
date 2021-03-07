package com.dbw.util;

import java.util.Collections;

public class StringUtils {

    public static String multiplyNTimes(int n, String str) {
        return String.join("", Collections.nCopies(n, str));
    }

}
