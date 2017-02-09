package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.view;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ViewBookDefinitionForm
{
    public static final String FORM_NAME = "viewBookDefinitionForm";

    public enum Command
    {
        EDIT,
        GENERATE,
        DELETE,
        AUDIT_LOG,
        BOOK_PUBLISH_STATS,
        COPY,
        RESTORE,
        GROUP
    };

    private Command command;
    private Long id;

    public Command getCommand()
    {
        return command;
    }

    public void setCommand(final Command cmd)
    {
        command = cmd;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(final Long id)
    {
        this.id = id;
    }

    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
