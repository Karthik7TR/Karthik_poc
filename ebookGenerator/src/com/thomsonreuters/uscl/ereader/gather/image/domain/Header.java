package com.thomsonreuters.uscl.ereader.gather.image.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * The required header in JSON format for the HTTP request body.
 * Example: "Header":{"ProductIdentifier":"Westlaw","UserHostIpAddress":"209.85.225.106"}
 */
public class Header {
    private String authenticationToken;
    private String contextualInformation;
    private String productIdentifier; // like "ebookGenerator"
    private String sessionToken;
    private String slideInformation;
    private String userHostIpAddress;
    private String version;

    @JsonProperty("AuthenticationToken")
    public String getAuthenticationToken() {
        return authenticationToken;
    }

    @JsonProperty("ContextualInformation")
    public String getContextualInformation() {
        return contextualInformation;
    }

    @JsonProperty("ProductIdentifier")
    public String getProductIdentifier() {
        return productIdentifier;
    }

    @JsonProperty("SessionToken")
    public String getSessionToken() {
        return sessionToken;
    }

    @JsonProperty("SlideInformation")
    public String getSlideInformation() {
        return slideInformation;
    }

    @JsonProperty("UserHostIpAddress")
    public String getUserHostIpAddress() {
        return userHostIpAddress;
    }

    @JsonProperty("Version")
    public String getVersion() {
        return version;
    }

    public void setAuthenticationToken(final String authenticationToken) {
        this.authenticationToken = authenticationToken;
    }

    public void setContextualInformation(final String contextualInformation) {
        this.contextualInformation = contextualInformation;
    }

    public void setProductIdentifier(final String productIdentifier) {
        this.productIdentifier = productIdentifier;
    }

    public void setSessionToken(final String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public void setSlideInformation(final String slideInformation) {
        this.slideInformation = slideInformation;
    }

    public void setUserHostIpAddress(final String dottedDecimalIpAddress) {
        userHostIpAddress = dottedDecimalIpAddress;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
