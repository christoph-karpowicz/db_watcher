package com.dbw.state;


import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class XmlColumnState {
    @JacksonXmlProperty(isAttribute = true)
    private String name = "name";
    @JacksonXmlText
    private String value;
}
