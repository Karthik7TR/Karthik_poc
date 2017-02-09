package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.delete;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class DeleteBookDefinitionForm
{
    public static final String FORM_NAME = "deleteBookDefinitionForm";

    public enum Action
    {
        DELETE,
        RESTORE
    };

    private Long id;
    private String comment;
    private String code;
    private Action action;

    public Long getId()
    {
        return id;
    }

    public void setId(final Long id)
    {
        this.id = id;
    }

    public String getComment()
    {
        return comment;
    }

    public void setComment(final String comment)
    {
        this.comment = comment;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(final String code)
    {
        this.code = code;
    }

    public Action getAction()
    {
        return action;
    }

    public void setAction(final Action action)
    {
        this.action = action;
    }

    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
