package com.dbw.state;

import java.util.ArrayList;
import java.util.List;

public class XmlStateTag {
    private final static String TAG_START = "<";
    private final static String TAG_END = ">";
    private final static String TAG_END_NAME_PREFIX = "/";
    
    private String name;
    List<XmlStateTagAttribute> attributes = new ArrayList<XmlStateTagAttribute>();
    List<String> baseTagComponents;

    public XmlStateTag(String name) {
        this.name = name;
        buildComponents();
    }

    private void buildComponents() {
        baseTagComponents = new ArrayList<String>();
        baseTagComponents.add(TAG_START);
        baseTagComponents.add(name);
        baseTagComponents.add(TAG_END);
    }

    public String start() {
        return String.join("", baseTagComponents);
    }

    public String end() {
        List<String> endTagComponents = new ArrayList<String>(baseTagComponents);
        endTagComponents.add(1, TAG_END_NAME_PREFIX);
        return String.join("", endTagComponents);
    }

}
