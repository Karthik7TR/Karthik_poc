package com.thomsonreuters.uscl.ereader.common.step;

import java.util.Date;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import org.jetbrains.annotations.NotNull;

public interface BookStep extends BaseStep
{
    /**
     * Should return {@code true} for initial step and {@code false} otherwise. Default value is
     * {@code false}.
     */
    boolean isInitialStep();

    @NotNull
    BookDefinition getBookDefinition();

    @NotNull
    Long getBookDefinitionId();

    @NotNull
    String getBookVersion();

    @NotNull
    String getUserName();

    @NotNull
    String getHostName();

    @NotNull
    String getEnvironment();

    @NotNull
    Date getSubmitTimestamp();
}
