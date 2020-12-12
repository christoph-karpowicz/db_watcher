package com.dbw.diff;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.dbw.db.Common;
import com.dbw.state.XmlColumnState;
import com.dbw.state.XmlColumnStates;
import com.dbw.state.XmlStateBuilder;
import com.dbw.state.XmlStateTag;
import com.dbw.state.XmlStateTagAttribute;
import com.dbw.util.HtmlInXmlEscaper;

@Singleton
public class XmlDiff extends Diff {
    
    @Inject
    private HtmlInXmlEscaper htmlInXmlEscaper;
    
    protected Map<String, Object> parseData(String data) throws Exception {
        Map<String, Object> parsedData = new LinkedHashMap<String, Object>();
        if (Strings.isNullOrEmpty(data)) {
            return ImmutableMap.copyOf(parsedData);
        }
        XmlStateTag xmlStateTag = new XmlStateTag(XmlStateBuilder.XML_COLUMN_STATE_TAG, true);
        XmlStateTagAttribute xmlStateTagAttribute = new XmlStateTagAttribute("name", ".+?", true);
        xmlStateTag.addAttribute(xmlStateTagAttribute);
        String escapedData = htmlInXmlEscaper.escapeHtmlBetweenXmlTags(data, xmlStateTag);
        XmlMapper xmlMapper = new XmlMapper();
        XmlColumnStates state = xmlMapper.readValue(escapedData, XmlColumnStates.class);
        for (XmlColumnState columnState : state.getColumnStates()) {
            Object columnStateValue = Objects.requireNonNullElse(columnState.getValue(), Common.NULL_AS_STRING);
            parsedData.put(columnState.getName(), columnStateValue);
        }
        return ImmutableMap.copyOf(parsedData);
    }

}
