package com.thomsonreuters.uscl.ereader.gather.image.domain;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.http.MediaType;

/**
 * Persisted metadata for images downloaded from the Image Vertical REST web service.
 */
@Entity
@Table(name = "IMAGE_METADATA")
public class ImageMetadataEntity implements Serializable {
    private static final long serialVersionUID = -3714413519050210417L;

    private ImageMetadataEntityKey primaryKey; // jobInstanceId & imageGuid
    private String titleId; // Identifies the book that this is for
    private Long width;
    private Long height;
    private Long size; // image size in bytes
    private Long dpi; // dots per inch
    private String dimUnits; // unit for dimensions, like "px" (pixels)
    /**
     * The desired/expected media type when downloading the image bytes.
     * Note that this type may be different from the media type returned by the request
     * for image metadata since we can request (via the Accept header) the desired image
     * media type and a conversion should occur within the Image Vertical REST service to the desired type.
     */
    private MediaType mediaType; // the desired/expected media type

    public ImageMetadataEntity() {
        super();
    }

    public ImageMetadataEntity(
        final ImageMetadataEntityKey key,
        final String titleId,
        final Long width,
        final Long height,
        final Long size,
        final Long dpi,
        final String dimUnits,
        final MediaType mediaType) {
        primaryKey = key;
        this.titleId = titleId;
        this.width = width;
        this.height = height;
        this.size = size;
        this.dpi = dpi;
        this.dimUnits = dimUnits;
        this.mediaType = mediaType;
    }

    /**
     * Primary key
     */
    @EmbeddedId
    @AttributeOverrides({
        @AttributeOverride(name = "jobInstanceId", column = @Column(name = "JOB_INSTANCE_ID", nullable = false)),
        @AttributeOverride(name = "imageGuid", column = @Column(name = "IMAGE_GUID", nullable = false)),
        @AttributeOverride(name = "docUuid", column = @Column(name = "DOC_UUID", nullable = false))})
    public ImageMetadataEntityKey getPrimaryKey() {
        return primaryKey;
    }

    @Column(name = "TITLE_ID", length = 64, nullable = false)
    public String getTitleId() {
        return titleId;
    }

    @Column(name = "IMAGE_WIDTH")
    public Long getWidth() {
        return width;
    }

    @Column(name = "IMAGE_HEIGHT")
    public Long getHeight() {
        return height;
    }

    @Column(name = "IMAGE_SIZE")
    public Long getSize() {
        return size;
    }

    @Column(name = "IMAGE_DPI")
    public Long getDpi() {
        return dpi;
    }

    @Column(name = "IMAGE_DIM_UNITS", length = 32)
    public String getDimUnits() {
        return dimUnits;
    }

    @Column(name = "MEDIA_TYPE", length = 64)
    public String getContentType() {
        return (mediaType != null) ? mediaType.toString() : null;
    }

    @Transient
    public MediaType getMediaType() {
        return mediaType;
    }

    public void setPrimaryKey(final ImageMetadataEntityKey pk) {
        primaryKey = pk;
    }

    public void setTitleId(final String titleId) {
        this.titleId = titleId;
    }

    public void setWidth(final Long width) {
        this.width = width;
    }

    public void setHeight(final Long height) {
        this.height = height;
    }

    public void setSize(final Long size) {
        this.size = size;
    }

    public void setDpi(final Long dpi) {
        this.dpi = dpi;
    }

    public void setDimUnits(final String dimUnits) {
        this.dimUnits = dimUnits;
    }

    public void setContentType(final String contentType) {
        mediaType = (StringUtils.isNotBlank(contentType)) ? MediaType.parseMediaType(contentType) : null;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
