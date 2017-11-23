package com.thomsonreuters.uscl.ereader.request.domain;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "web_build_product_type")
@XmlEnum
public enum XppBundleWebBuildProductType {
    @XmlEnumValue("Bound Volume")
    BOUND_VOLUME(false),
    @XmlEnumValue("Looseleaf Supplement")
    LOOSELEAF_SUPPLEMENT(true),
    @XmlEnumValue("Looseleaf Update")
    LOOSELEAF_UPDATE(false),
    @XmlEnumValue("Looseleaf Content")
    LOOSELEAF_CONTENT(false),
    @XmlEnumValue("Binder Pamphlet Supplement")
    BINDER_PAMPHLET_SUPPLEMENT(true),
    @XmlEnumValue("Binder Pamphlet")
    BINDER_PAMPHLET(false),
    @XmlEnumValue("Newsletter")
    NEWSLETTER(false),
    @XmlEnumValue("Pocket Part")
    POCKET_PART(true),
    @XmlEnumValue("Supplementary Pamphlet")
    SUPPLEMENTARY_PAMPHLET(true),
    @XmlEnumValue("Pamphlet")
    PAMPHLET(false);

    private final boolean isPocketPartType;

    XppBundleWebBuildProductType(final boolean isPocketPartType) {
        this.isPocketPartType = isPocketPartType;
    }

    public boolean isPocketPart() {
        return isPocketPartType;
    }
}
