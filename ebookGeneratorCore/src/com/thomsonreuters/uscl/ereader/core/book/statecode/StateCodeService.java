package com.thomsonreuters.uscl.ereader.core.book.statecode;

import java.util.List;

import org.jetbrains.annotations.NotNull;

public interface StateCodeService {
    /**
     * Get all the State codes
     * @return a list of StateCode objects
     */
    List<StateCode> getAllStateCodes();

    /**
     * Get a State Code by Id
     * @param stateCodeId
     */
    StateCode getStateCodeById(@NotNull Long stateCodeId);

    /**
     * Get a State Code by name
     * @param stateCodeName
     */
    StateCode getStateCodeByName(@NotNull String stateCodeName);

    /**
     * Create or Update a State Code
     * @param stateCode
     */
    void saveStateCode(@NotNull StateCode stateCode);

    /**
     * Delete a State Code
     * @param stateCode
     */
    void deleteStateCode(@NotNull StateCode stateCode);
}
