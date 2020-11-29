package com.dbw.diff;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.dbw.db.Common;
import com.dbw.state.XmlColumnState;
import com.dbw.state.XmlColumnStates;
import com.dbw.state.XmlStateBuilder;

public class XmlDiff extends Diff {
    
    protected Map<String, Object> parseData(String data) throws Exception {
        Map<String, Object> parsedData = new LinkedHashMap<String, Object>();
        if (Strings.isNullOrEmpty(data)) {
            return ImmutableMap.copyOf(parsedData);
        }
        String escapedData = escapeHtml(data);
        XmlMapper xmlMapper = new XmlMapper();
        XmlColumnStates state = xmlMapper.readValue(escapedData, XmlColumnStates.class);
        for (XmlColumnState columnState : state.getColumnStates()) {
            String columnStateValue = Objects.isNull(columnState.getValue()) ? Common.NULL_AS_STRING : columnState.getValue();
            parsedData.put(columnState.getName(), columnStateValue);
        }
        return ImmutableMap.copyOf(parsedData);
    }

    private String escapeHtml(String data) {
        String escapedData = data;
        String xmlColumnStateStartTag = "<" + XmlStateBuilder.XML_COLUMN_STATE_TAG + "\sname=\".+?\">";
        Pattern xmlColumnStateStartTagPattern = Pattern.compile(xmlColumnStateStartTag);
        String xmlColumnStateEndTag = "<\\/" + XmlStateBuilder.XML_COLUMN_STATE_TAG + ">";
        Pattern xmlColumnStateEndTagPattern = Pattern.compile(xmlColumnStateEndTag);
        Pattern xmlColumnStateTagWithContentPattern = Pattern.compile(xmlColumnStateStartTag + ".+?" + xmlColumnStateEndTag);
        Matcher columnStateTagsContentsMatcher = xmlColumnStateTagWithContentPattern.matcher(escapedData);

        while (columnStateTagsContentsMatcher.find()) {
            Matcher columnStateStartTagMatcher = xmlColumnStateStartTagPattern.matcher(columnStateTagsContentsMatcher.group());
            String content = columnStateStartTagMatcher.replaceAll("");
            Matcher columnStateEndTagMatcher = xmlColumnStateEndTagPattern.matcher(content);
            content = columnStateEndTagMatcher.replaceAll("");

            String[][] htmlCharsWithReplacements = new String[][]{
                {"&", "&amp;"},
                {">", "&gt;"},
                {"<", "&lt;"},
                {"\"", "&quot;"},
                {"'", "&apos;"},
                {"/", "&#47;"},
                {"\\", "&#92;"}
            };
            String escapedContent = content;
            for (String[] htmlCharWithReplacement : htmlCharsWithReplacements) {
                escapedContent = escapedContent.replace(htmlCharWithReplacement[0], htmlCharWithReplacement[1]);
            }
            escapedData = escapedData.replace(content, escapedContent);
        }
        return escapedData;
    }

}
