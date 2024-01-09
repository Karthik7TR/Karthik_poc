package com.thomsonreuters.uscl.ereader.core.book.statecode;

import org.jetbrains.annotations.NotNull;

public final class StateCodeTestUtil {
    private StateCodeTestUtil() {
    }

    @NotNull
    public static StateCode stateCode(final Long id) {
        final StateCode stateCode = new StateCode();
        stateCode.setId(id);
        return stateCode;
    }
}
