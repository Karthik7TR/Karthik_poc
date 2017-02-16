package com.thomsonreuters.uscl.ereader.core.book.statecode;

import java.sql.Timestamp;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;

public final class StateCodeTestUtil
{
    private StateCodeTestUtil()
    {
    }

    @NotNull
    public static StateCode stateCode(final Long id, final String name, final DateTime timestamp)
    {
        final StateCode stateCode = stateCode(id);
        stateCode.setName(name);
        stateCode.setLastUpdated(new Timestamp(timestamp.toDate().getTime()));
        return stateCode;
    }

    @NotNull
    public static StateCode stateCode(final Long id)
    {
        final StateCode stateCode = new StateCode();
        stateCode.setId(id);
        return stateCode;
    }
}
