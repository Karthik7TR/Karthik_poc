package com.thomsonreuters.uscl.ereader.proview;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang3.StringUtils;

/**
 * Represents a doc within title.xml.
 *
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Doc implements Serializable {
    private static final long serialVersionUID = 1L;
    @XmlAttribute
    private String id;
    @XmlAttribute
    private String src;
    @XmlTransient
    private int splitTitlePart;
    private List<String> imageIdList;

    public Doc() {
    }

    public Doc(final String id, final String src, final int splitTitlePart, final List<String> imageIdList) {
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("'id' is a required field on <doc>.");
        }
        if (StringUtils.isBlank(src)) {
            throw new IllegalArgumentException("'src' is a required field on <doc>.");
        }
        if (splitTitlePart > 0) {
            this.splitTitlePart = splitTitlePart;
        }
        if (imageIdList != null && imageIdList.size() > 0) {
            this.imageIdList = imageIdList;
        }
        this.id = id;
        this.src = src;
    }

    public String getSrc() {
        return src;
    }

    public String getId() {
        return id;
    }

    public int getSplitTitlePart() {
        return splitTitlePart;
    }

    public void setSplitTitlePart(final int splitTitlePart) {
        this.splitTitlePart = splitTitlePart;
    }

    public List<String> getImageIdList() {
        return imageIdList;
    }

    public void setImageIdList(final List<String> imageIdList) {
        this.imageIdList = (imageIdList == null) ? new ArrayList<>() : imageIdList;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, src);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        boolean isEqual = false;
        if (obj instanceof Doc) {
            final Doc doc = (Doc) obj;
            isEqual = Objects.equals(id, doc.id) && Objects.equals(src, doc.src);
        }
        return isEqual;
    }

    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("Doc [id=").append(id).append(", ");
        buffer.append("src=").append(src).append(", ");
        buffer.append("splitTitlePart=").append(splitTitlePart);
        if (imageIdList != null)
            buffer.append(", ").append("imgSize=").append(imageIdList.size()).append(", ");
        buffer.append("]");

        return buffer.toString();
    }
}
