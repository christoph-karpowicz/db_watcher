package com.dbw.diff;

import com.dbw.db.Common;
import com.dbw.state.*;
import com.dbw.util.HtmlInXmlEscaper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Singleton
public class XmlDiff extends Diff {
    @Inject
    private HtmlInXmlEscaper htmlInXmlEscaper;
    
    protected Map<String, Object> parseData(String data) throws JsonProcessingException {
        Map<String, Object> parsedData = new LinkedHashMap<>();
        if (Strings.isNullOrEmpty(data)) {
            return ImmutableMap.copyOf(parsedData);
        }
        XmlStateTag xmlStateTag = new XmlStateTag(XmlStateBuilder.XML_COLUMN_STATE_TAG, true);
        XmlStateTagAttribute xmlStateTagAttribute = new XmlStateTagAttribute(XmlStateBuilder.XML_COLUMN_STATE_NAME_ATTRIBUTE, ".+?", true);
        xmlStateTag.addAttribute(xmlStateTagAttribute);
        if (htmlInXmlEscaper.isHtmlBetweenXmlTags(data)) {
            data = htmlInXmlEscaper.escapeHtmlBetweenXmlTags(data, xmlStateTag);
        }
        XmlMapper xmlMapper = new XmlMapper();
        data = XmlStateBuilder.XML_DECLARATION + data;
        XmlColumnStates state = xmlMapper.readValue(data, XmlColumnStates.class);
        for (XmlColumnState columnState : state.getColumnStates()) {
            Object columnStateValue = Optional.ofNullable(columnState.getValue()).orElse(Common.NULL_AS_STRING);
            parsedData.put(columnState.getName(), columnStateValue);
        }
        return ImmutableMap.copyOf(parsedData);
    }

}
