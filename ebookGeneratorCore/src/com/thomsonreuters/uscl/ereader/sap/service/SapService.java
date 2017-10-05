package com.thomsonreuters.uscl.ereader.sap.service;

import com.thomsonreuters.uscl.ereader.sap.component.Material;
import com.thomsonreuters.uscl.ereader.smoketest.domain.SmokeTest;
import org.jetbrains.annotations.NotNull;

/**
 * SAP service provides methods to get materials
 */
public interface SapService {
    /**
     * Get materials by material number
     */
    @NotNull
    Material getMaterialByNumber(@NotNull String materialNumber);

    @NotNull
    SmokeTest checkSapStatus();
}
