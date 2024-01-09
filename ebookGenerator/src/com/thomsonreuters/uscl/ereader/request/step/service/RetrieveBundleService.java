package com.thomsonreuters.uscl.ereader.request.step.service;

import java.io.File;

import com.thomsonreuters.uscl.ereader.request.XppMessageException;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundleArchive;
import org.jetbrains.annotations.NotNull;

public interface RetrieveBundleService {
    void retrieveBundle(@NotNull XppBundleArchive request, @NotNull File bundleDestDir) throws XppMessageException;
}
