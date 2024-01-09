package com.thomsonreuters.uscl.ereader.mgr.web.controller;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * An informational message to be provided to the user.
 * The Type is a hint to the view as to how the message text should be rendered (color, font, etc...).
 * For example an error may be rendered in red, while success message text may be displayed in green
 */
public class InfoMessage {
    public enum Type {
        SUCCESS,
        GENERAL,
        INFO,
        WARNING,
        ERROR,
        FAIL
    }

    private Type type;
    private String text;

    public InfoMessage(final Type type, final String text) {
        this.type = type;
        this.text = text;
    }

    public Type getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
