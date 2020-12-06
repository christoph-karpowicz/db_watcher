package com.dbw.state;

import java.util.ArrayList;
import java.util.List;

public class XmlStateTag {
    private final static String SPACE = " ";
    private final static String TAG_START = "'<";
    private final static String TAG_END = ">'";
    private final static String TAG_END_NAME_PREFIX = "/";
    private final static String REGEX_SPACE = "\\s";
    private final static String REGEX_TAG_START = "<";
    private final static String REGEX_TAG_END = ">";
    
    private String name;
    List<XmlStateTagAttribute> attributes = new ArrayList<XmlStateTagAttribute>();
    List<String> baseTagComponents;
    private boolean isRegex = false;

    public XmlStateTag(String name) {
        this.name = name;
        buildComponents();
    }

    public XmlStateTag(String name, boolean isRegex) {
        this.name = name;
        this.isRegex = isRegex;
        buildComponents();
    }

    private void buildComponents() {
        baseTagComponents = new ArrayList<String>();
        baseTagComponents.add(isRegex ? REGEX_TAG_START : TAG_START);
        baseTagComponents.add(name);
        baseTagComponents.add(isRegex ? REGEX_TAG_END : TAG_END);
    }

    public void addAttribute(XmlStateTagAttribute attribute) {
        attributes.add(attribute);
    }

    public void resetAttributes() {
        attributes.clear();
    }

    public String startTag() {
        List<String> startTagComponents = new ArrayList<String>(baseTagComponents);
        for (XmlStateTagAttribute attr : attributes) {
            startTagComponents.add(2, isRegex ? REGEX_SPACE : SPACE);
            startTagComponents.add(3, attr.toString());
        }
        return String.join("", startTagComponents);
    }

    public String endTag() {
        List<String> endTagComponents = new ArrayList<String>(baseTagComponents);
        String tagEndNamePrefix = !isRegex ? TAG_END_NAME_PREFIX : "\\" + TAG_END_NAME_PREFIX;
        endTagComponents.add(1, tagEndNamePrefix);
        return String.join("", endTagComponents);
    }

}
