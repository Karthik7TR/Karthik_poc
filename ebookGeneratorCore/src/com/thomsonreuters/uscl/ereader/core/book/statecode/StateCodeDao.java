package com.thomsonreuters.uscl.ereader.core.book.statecode;

import java.util.List;

import org.jetbrains.annotations.NotNull;

public interface StateCodeDao {
    /**
     * Create State Code
     */
    void createStateCode(@NotNull StateCode stateCode);

    /**
     * Update State Code
     */
    void updateStateCode(@NotNull StateCode stateCode);

    /**
     * Get all the State codes from the STATE_CODES table
     *
     * @return a list of StateCode objects
     */
    List<StateCode> getAllStateCodes();

    /**
     * Get a State Code with given ID
     */
    StateCode getStateCodeById(@NotNull Long stateCodeId);

    /**
     * Get a State Code with given name
     */
    StateCode getStateCodeByName(@NotNull String stateCodeName);

    /**
     * Delete a State Code
     */
    void deleteStateCode(@NotNull StateCode stateCode);
}
