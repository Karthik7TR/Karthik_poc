package com.thomsonreuters.uscl.ereader.proview;

import java.io.Serializable;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.apache.commons.lang3.StringUtils;

/**
 * This class represents the cover art for a given title.
 *
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Artwork implements Serializable{
    private static final long serialVersionUID = 3L;
    @XmlAttribute(name = "src")
    private String src;
    @XmlAttribute(name = "type")
    private String type = "cover";

    public Artwork() {
        //Intentionally left blank
    }

    public Artwork(final String src) {
        if (StringUtils.isBlank(src)) {
            throw new IllegalArgumentException("'src' parameter is required in order to create cover art.");
        }
        this.src = src;
    }

    public String getSrc() {
        return src;
    }

    public String getType() {
        return type;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Artwork)) {
            return false;
        }
        final Artwork artwork = (Artwork) obj;
        return Objects.equals(src, artwork.src) && Objects.equals(type, artwork.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(src, type);
    }
}
