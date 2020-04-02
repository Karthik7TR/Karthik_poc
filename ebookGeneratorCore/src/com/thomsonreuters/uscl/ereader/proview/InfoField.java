package com.thomsonreuters.uscl.ereader.proview;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
@AllArgsConstructor
@Data
public class InfoField {
    @XmlElement(name = "header")
    private String header;
    @XmlElement(name = "note")
    private String note;
}
