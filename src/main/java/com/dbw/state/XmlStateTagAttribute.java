package com.dbw.state;

public class XmlStateTagAttribute {
    private final String EQUALS_START = "=\"";
    private final String EQUALS_END = "\"";
    private final String REGEX_EQUALS_START = "=\\\"";
    private final String REGEX_EQUALS_END = "\\\"";
    
    private String name;
    private String value;
    private boolean isRegex = false;

    public XmlStateTagAttribute(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public XmlStateTagAttribute(String name, String value, boolean isRegex) {
        this.name = name;
        this.value = value;
        this.isRegex = isRegex;
    }

    @Override
    public String toString() {
        StringBuilder attrBuilder = new StringBuilder();
        attrBuilder.append(name);
        attrBuilder.append(isRegex ? REGEX_EQUALS_START : EQUALS_START);
        attrBuilder.append(value);
        attrBuilder.append(isRegex ? REGEX_EQUALS_END : EQUALS_END);
        return attrBuilder.toString();
    }
}
