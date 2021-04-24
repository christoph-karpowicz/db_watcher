package com.dbw.frame;

import com.dbw.output.OutputBuilder;
import com.dbw.util.StringUtils;

public class AuditFrameHeaderColumn implements OutputBuilder {
    private final String header;
    private final String value;
    private final int width;

    public AuditFrameHeaderColumn(String header, String value) {
        this.header = header;
        this.value = value;
        this.width = calculateWidth();
    }

    private int calculateWidth() {
        return Math.max(header.length(), value.length());
    }

    public String getHeader() {
        return getWithPadding(header);
    }

    public String getValue() {
        return getWithPadding(value);
    }

    private String getWithPadding(String val) {
        return val + StringUtils.multiplyNTimes(width - val.length(), PADDING);
    }
}
