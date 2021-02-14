package com.dbw.cache;

import java.io.Serializable;

public class ConfigCache implements Serializable {
    private static final long serialVersionUID = 2L;
    private String checksum;

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }
}
