package com.dbw.state;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@RequiredArgsConstructor
public class XmlStateTagAttribute {
    private final String EQUALS_START = "=\"";
    private final String EQUALS_END = "\"";
    private final String REGEX_EQUALS_START = "=\\\"";
    private final String REGEX_EQUALS_END = "\\\"";
    
    private final String name;
    private final String value;
    private boolean isRegex = false;

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
