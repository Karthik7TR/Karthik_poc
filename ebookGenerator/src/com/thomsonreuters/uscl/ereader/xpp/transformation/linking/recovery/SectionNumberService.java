package com.thomsonreuters.uscl.ereader.xpp.transformation.linking.recovery;

import org.jetbrains.annotations.NotNull;

public interface SectionNumberService {
    @NotNull
    String getPattern();

    @NotNull
    String getPrefixPattern();
}
