package com.dbw.state;

public class XmlStateTagAttribute {
    private final String EQUALS_START = "=\"";
    private final String EQUALS_END = "\"";
    
    private String name;
    private String value;

    public XmlStateTagAttribute(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        StringBuilder attrBuilder = new StringBuilder();
        attrBuilder.append(name);
        attrBuilder.append(EQUALS_START);
        attrBuilder.append(value);
        attrBuilder.append(EQUALS_END);
        return attrBuilder.toString();
    }
}
