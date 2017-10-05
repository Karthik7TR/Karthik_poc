package com.thomsonreuters.uscl.ereader.proview;

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Represents an asset within title.xml
 *
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Asset {
    @XmlAttribute(name = "id")
    private String id;
    @XmlAttribute(name = "src")
    private String src;

    public Asset() {
    }

    public Asset(final String id, final String src) {
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("'id' is a required field.");
        }
        if (StringUtils.isBlank(src)) {
            throw new IllegalArgumentException("'src' is a required field.");
        }
        this.id = id;
        this.src = src;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public String getId() {
        return id;
    }

    public String getSrc() {
        return src;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Asset)) {
            return false;
        }
        final Asset equalCheck = (Asset) obj;
        return Objects.equals(id, equalCheck.id) && Objects.equals(src, equalCheck.src);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, src);
    }
}
