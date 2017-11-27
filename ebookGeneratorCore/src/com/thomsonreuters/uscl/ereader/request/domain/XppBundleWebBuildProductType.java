package com.thomsonreuters.uscl.ereader.request.domain;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "web_build_product_type")
@XmlEnum
public enum XppBundleWebBuildProductType {
    @XmlEnumValue("Bound Volume")
    BOUND_VOLUME("Bound Volume", false),
    @XmlEnumValue("Looseleaf Supplement")
    LOOSELEAF_SUPPLEMENT("Supplement", true),
    @XmlEnumValue("Looseleaf Update")
    LOOSELEAF_UPDATE("Looseleaf Update", false),
    @XmlEnumValue("Looseleaf Content")
    LOOSELEAF_CONTENT("Looseleaf Content", false),
    @XmlEnumValue("Binder Pamphlet Supplement")
    BINDER_PAMPHLET_SUPPLEMENT("Supplement", true),
    @XmlEnumValue("Binder Pamphlet")
    BINDER_PAMPHLET("Binder Pamphlet", false),
    @XmlEnumValue("Newsletter")
    NEWSLETTER("Newsletter", false),
    @XmlEnumValue("Pocket Part")
    POCKET_PART("Pocket Part", true),
    @XmlEnumValue("Supplementary Pamphlet")
    SUPPLEMENTARY_PAMPHLET("Supplement", true),
    @XmlEnumValue("Pamphlet")
    PAMPHLET("Pamphlet", false);

    private final String humanReadableName;
    private final boolean isPocketPartType;

    XppBundleWebBuildProductType(final String humanReadableName, final boolean isPocketPartType) {
        this.humanReadableName = humanReadableName;
        this.isPocketPartType = isPocketPartType;
    }

    public String getHumanReadableName() {
        return humanReadableName;
    }

    public boolean isPocketPart() {
        return isPocketPartType;
    }
}
