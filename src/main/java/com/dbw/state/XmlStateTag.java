package com.dbw.state;

import java.util.ArrayList;
import java.util.List;

public class XmlStateTag {
    private final static String SPACE = " ";
    private final static String TAG_START = "'<";
    private final static String TAG_END = ">'";
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

    public void addAttribute(XmlStateTagAttribute attribute) {
        attributes.add(attribute);
    }

    public void resetAttributes() {
        attributes.clear();
    }

    public String start() {
        List<String> startTagComponents = new ArrayList<String>(baseTagComponents);
        for (XmlStateTagAttribute attr : attributes) {
            startTagComponents.add(2, SPACE);
            startTagComponents.add(3, attr.toString());
        }
        return String.join("", startTagComponents);
    }

    public String end() {
        List<String> endTagComponents = new ArrayList<String>(baseTagComponents);
        endTagComponents.add(1, TAG_END_NAME_PREFIX);
        return String.join("", endTagComponents);
    }

}
